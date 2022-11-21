package com.ishan.retailservice.invoicefanout.port.adapters.http;

import com.ishan.retailservice.invoicefanout.domain.BestSellerService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/best-sellers")
public class BestSellersController {

  @Autowired
  private BestSellerService bestSellerService;

  @GetMapping
  public ResponseEntity<List<String>> getBestSellers() {
    return ResponseEntity.ok(bestSellerService.getTopNBestSellingProducts(5));
  }

}
