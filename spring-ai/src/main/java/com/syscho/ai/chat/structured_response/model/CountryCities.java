package com.syscho.ai.chat.structured_response.model;

import java.util.List;

public record CountryCities(String country, List<String> cities) {
}