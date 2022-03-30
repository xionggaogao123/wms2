package com.huanhong.common.units.weixin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageKeyWord {

    private String value;
    private String color;

    public MessageKeyWord(String value) {
        this.value = value;
    }
}
