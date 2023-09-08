package com.benewake.saleordersystem.service;

import org.springframework.stereotype.Service;

@Service
public interface FeiShuMessageService {
    void sendMessage(String salemanName, String message);
}
