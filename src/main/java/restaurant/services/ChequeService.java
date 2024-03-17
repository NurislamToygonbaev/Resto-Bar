package restaurant.services;

import restaurant.dto.request.ChequeSaveRequest;
import restaurant.dto.request.ChequeUpdateRequest;
import restaurant.dto.response.ChequePagination;
import restaurant.dto.response.ChequeResponses;
import restaurant.dto.response.SimpleResponse;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;

public interface ChequeService {
    ChequePagination findAllCheques(int page, int size, Principal principal);

    ChequeResponses findById(Long chequeId, Principal principal);

    SimpleResponse updateCheque(Long chequeId, ChequeUpdateRequest request, Principal principal);

    SimpleResponse delete(Long chequeId, Principal principal);

    ChequeResponses saveCheque(ChequeSaveRequest request, Principal principal);

    BigDecimal totalPriceInDay(Principal principal, LocalDate date);

    BigDecimal avfSumByAdmin(Principal principal, LocalDate date);
}
