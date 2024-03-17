package restaurant.api;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import restaurant.services.ChequeService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cheque")
public class ChequeApi {
    private final ChequeService chequeService;

//    @PostMapping("/{menuId}/{userId}")
//    public
}
