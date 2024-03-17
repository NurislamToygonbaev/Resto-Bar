package restaurant.api;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import restaurant.dto.request.ChequeSaveRequest;
import restaurant.dto.request.ChequeUpdateRequest;
import restaurant.dto.response.ChequePagination;
import restaurant.dto.response.ChequeResponses;
import restaurant.dto.response.SimpleResponse;
import restaurant.services.ChequeService;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cheque")
public class ChequeApi {
    private final ChequeService chequeService;

    @Secured({"ADMIN", "WAITER", "CHEF"})
    @GetMapping
    public ChequePagination findAll(Principal principal,
                                    @RequestParam int page,
                                    @RequestParam int size){
        return chequeService.findAllCheques(page, size, principal);
    }

    @Secured({"ADMIN", "WAITER", "CHEF"})
    @GetMapping("/find/{chequeId}")
    public ChequeResponses findById(@PathVariable Long chequeId, Principal principal){
        return chequeService.findById(chequeId, principal);
    }

    @Secured("ADMIN")
    @PutMapping("/{chequeId}")
    public SimpleResponse update(@PathVariable Long chequeId,
                                 Principal principal,
                                 @RequestBody ChequeUpdateRequest request){
        return chequeService.updateCheque(chequeId, request, principal);
    }

    @Secured("ADMIN")
    @DeleteMapping("/{chequeId}")
    public SimpleResponse delete(@PathVariable Long chequeId, Principal principal){
        return chequeService.delete(chequeId, principal);
    }

    @Secured({"ADMIN", "WAITER"})
    @PostMapping
    public ChequeResponses saveCheque(@RequestBody ChequeSaveRequest request,
                                      Principal principal){
        return chequeService.saveCheque(request, principal);
    }

    @Secured({"ADMIN", "WAITER"})
    @GetMapping("/total")
    public BigDecimal totalPriceInDay(Principal principal,
                                      @RequestParam LocalDate date){
        return chequeService.totalPriceInDay(principal, date);
    }

    @Secured("ADMIN")
    @GetMapping("/avg")
    public BigDecimal avgSumByAdmin(Principal principal,
                                    @RequestParam LocalDate date){
        return chequeService.avfSumByAdmin(principal, date);
    }
}
