package restaurant.services.impl;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import restaurant.dto.request.ChequeSaveRequest;
import restaurant.dto.request.ChequeUpdateRequest;
import restaurant.dto.response.*;
import restaurant.entities.*;
import restaurant.exceptions.BedRequestException;
import restaurant.repository.ChequeRepository;
import restaurant.repository.MenuItemRepository;
import restaurant.repository.StopListRepository;
import restaurant.services.ChequeService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChequeServiceImpl implements ChequeService {
    private final ChequeRepository chequeRepo;
    private final CurrentUserService currentUserService;
    private final MenuItemRepository menuItemRepo;
    private final StopListRepository stopListRepo;

    @Override
    public ChequePagination findAllCheques(int page, int size, Principal principal) {
        User user = currentUserService.adminAndChefAndWaiter(principal);
        Long resId = user.getRestaurant().getId();
        Pageable pageable = PageRequest.of(page - 1, size);

        Page<Cheque> cheques = chequeRepo.getAllChequesByResId(resId, pageable);

        List<ChequeResponses> collected = cheques.getContent().stream()
                .map(this::convertToCheque)
                .collect(Collectors.toList());
        return ChequePagination.builder()
                .page(cheques.getNumber() + 1)
                .size(cheques.getNumberOfElements())
                .responses(collected)
                .build();
    }

    private ChequeResponses convertToCheque(Cheque cheque) {
        User user = cheque.getUser();
        Restaurant restaurant = user.getRestaurant();
        String service = String.valueOf(restaurant.getService());
        int servicePercent = restaurant.getService();
        BigDecimal totalPrice = sumPrice(cheque.getMenuItems());

        BigDecimal serviceAmount = totalPrice.multiply(BigDecimal.valueOf(servicePercent / 100.0));
        BigDecimal grandTotalPrice = totalPrice.add(serviceAmount);

        List<MenuItemsResponseForCheque> collected = cheque.getMenuItems().stream()
                .map(menuItem -> new MenuItemsResponseForCheque(menuItem.getName(),
                        menuItem.getImage(), String.valueOf(menuItem.getPrice())+" som",
                        menuItem.getDescription(), menuItem.isVegetarian()
                ))
                .collect(Collectors.toList());
        return new ChequeResponses(
                user.getFirstName() + " " + user.getLastName(), collected,
                String.valueOf(totalPrice) +" som",
                service + " %", String.valueOf(grandTotalPrice)+" som", cheque.getCreatedAt()
        );
    }

    @Override
    public ChequeResponses findById(Long chequeId, Principal principal) {
        User user = currentUserService.adminAndChefAndWaiter(principal);
        Restaurant adminRestaurant = user.getRestaurant();

        Cheque cheque = chequeRepo.getChequeById(chequeId);
        Restaurant userRestaurant = cheque.getUser().getRestaurant();
        int servicePercent = userRestaurant.getService();
        currentUserService.checkForbidden(adminRestaurant, userRestaurant);

        List<MenuItemsResponseForCheque> forCheques = chequeRepo.convertToMenu(chequeId);
        BigDecimal price = sumPrice(cheque.getMenuItems());

        BigDecimal serviceAmount = price.multiply(BigDecimal.valueOf(servicePercent / 100.0));
        BigDecimal grandTotalPrice = price.add(serviceAmount);

        return ChequeResponses.builder()
                .fullName(user.getFirstName() + " " + user.getLastName())
                .responses(forCheques)
                .priceAvg(String.valueOf(price) + " som")
                .service(String.valueOf(userRestaurant.getService() + " %"))
                .totalSum(String.valueOf(grandTotalPrice) +" som")
                .createdAt(cheque.getCreatedAt())
                .build();
    }

    @Override
    public SimpleResponse updateCheque(Long chequeId, ChequeUpdateRequest request, Principal principal) {
        User user = currentUserService.adminUser(principal);
        Restaurant adminRestaurant = user.getRestaurant();
        Cheque cheque = chequeRepo.getChequeById(chequeId);
        Restaurant userRestaurant = cheque.getUser().getRestaurant();
        currentUserService.checkForbidden(adminRestaurant, userRestaurant);

        cheque.setPriceAvg(BigDecimal.valueOf(request.priceAvg()));
        chequeRepo.save(cheque);

        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("Cheque Successfully updated")
                .build();
    }

    @Override
    public SimpleResponse delete(Long chequeId, Principal principal) {
        User user = currentUserService.adminUser(principal);
        Restaurant adminRestaurant = user.getRestaurant();
        Cheque cheque = chequeRepo.getChequeById(chequeId);
        Restaurant userRestaurant = cheque.getUser().getRestaurant();
        currentUserService.checkForbidden(adminRestaurant, userRestaurant);

        chequeRepo.delete(cheque);

        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("Cheque Successfully deleted")
                .build();
    }

    @Override
    public ChequeResponses saveCheque(ChequeSaveRequest request, Principal principal) {
        User user = currentUserService.adminAndWaiter(principal);
        Restaurant restaurant = user.getRestaurant();
        List<Long> menuIds = request.menuIds();

        Cheque cheque = new Cheque();
        cheque.setCreatedAt(LocalDate.now());
        BigDecimal summedPrice = BigDecimal.ZERO;

        for (Long menuId : menuIds) {
            MenuItem menuItem = menuItemRepo.getMenuById(menuId);

            if (menuItem.getStopList() != null){
                throw new BedRequestException("menu in the stop list");
            }
            if (menuItem.getQuantity() == 0) {
                StopList stopList = new StopList();
                stopList.setReason("There are no more");
                stopList.setDate(LocalDate.now());
                stopList.setMenuItem(menuItem);
                menuItem.setStopList(stopList);
                stopListRepo.save(stopList);
                throw new BedRequestException("Menu item is not available: " + menuItem.getName());
            }

            BigDecimal price = menuItem.getPrice();
            summedPrice = summedPrice.add(price);

            menuItem.setQuantity(menuItem.getQuantity() - 1);
            menuItem.addCheque(cheque);
            cheque.addMenuItem(menuItem);

        }

        chequeRepo.save(cheque);
        cheque.setUser(user);
        user.addCheque(cheque);
        menuItemRepo.saveAll(menuIds.stream()
                .map(menuItemRepo::getMenuById)
                .collect(Collectors.toList()));

        int servicePercent = restaurant.getService();
        BigDecimal serviceAmount = summedPrice.multiply(BigDecimal.valueOf(servicePercent / 100.0));
        BigDecimal grandTotalPrice = summedPrice.add(serviceAmount);
        return ChequeResponses.builder()
                .fullName(user.getFirstName() + " " + user.getLastName())
                .responses(convertChequeItemsToResponse(cheque.getMenuItems()))
                .priceAvg(String.valueOf(summedPrice) + " som")
                .service(String.valueOf(servicePercent) + " %")
                .totalSum(String.valueOf(grandTotalPrice) + " som")
                .createdAt(cheque.getCreatedAt())
                .build();
    }
    private List<MenuItemsResponseForCheque> convertChequeItemsToResponse(List<MenuItem> menuItems) {
        List<MenuItemsResponseForCheque> responses = new ArrayList<>();
        for (MenuItem menuItem : menuItems) {
            MenuItemsResponseForCheque menuRes = new MenuItemsResponseForCheque(
                    menuItem.getName(), menuItem.getImage(), String.valueOf(menuItem.getPrice())+" som",
                    menuItem.getDescription(), menuItem.isVegetarian()
            );
            responses.add(menuRes);
        }
        return responses;
    }
    private BigDecimal sumPrice(List<MenuItem> menuItems) {
        return menuItems.stream()
                .map(MenuItem::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public BigDecimal totalPriceInDay(Principal principal, LocalDate date) {
        User user = currentUserService.adminAndWaiter(principal);
        List<Cheque> cheques = user.getCheques();
        BigDecimal totalSum = BigDecimal.ZERO;
        LocalDate now = LocalDate.now();
        if (date.isAfter(now)){
            throw new BedRequestException("cannot be in the future");
        }

        for (Cheque cheque : cheques) {
            LocalDate createdAt = cheque.getCreatedAt();
            if (createdAt.equals(date)){
                totalSum = totalSum.add(cheque.getPriceAvg());
            }
        }
        return totalSum;
    }

    @Override
    public BigDecimal avfSumByAdmin(Principal principal, LocalDate date) {
        User user = currentUserService.adminUser(principal);
        Restaurant restaurant = user.getRestaurant();
        List<MenuItem> menuItems = restaurant.getMenuItems();
        LocalDate now = LocalDate.now();
        if (date.isAfter(now)){
            throw new BedRequestException("cannot be in the future");
        }

        List<Cheque> cheques = new ArrayList<>();
        for (MenuItem menuItem : menuItems) {
            cheques.addAll(menuItem.getCheques());
        }

        BigDecimal totalSum = BigDecimal.ZERO;
        int count = 0;
        for (Cheque cheque : cheques) {
            LocalDate createdAtDate = cheque.getCreatedAt();
            if (createdAtDate.equals(date)) {
                totalSum = totalSum.add(cheque.getPriceAvg());
                count++;
            }
        }

        if (count == 0) {
            return BigDecimal.ZERO;
        }

        return totalSum.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP);
    }
}
