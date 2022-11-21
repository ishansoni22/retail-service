package com.ishan.retailservice.invoicefanout.port.adapters.service;

import com.ishan.retailservice.invoicefanout.domain.BestSellerService;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisBestSellerService implements BestSellerService {

  private static final String BEST_SELLERS_KEY = "products:best-sellers";

  @Autowired
  private RedisTemplate<String, Object> redisTemplate;

  @Override
  public void registerProductPurchase(String product, int quantity) {
    redisTemplate.opsForZSet().incrementScore(BEST_SELLERS_KEY, product, quantity);
  }

  @Override
  public List<String> getTopNBestSellingProducts(int n) {
    Set<String> bestSellers = (Set) redisTemplate.opsForZSet()
        .reverseRange(BEST_SELLERS_KEY, 0, n);
    return new ArrayList<>(bestSellers);
  }

}
