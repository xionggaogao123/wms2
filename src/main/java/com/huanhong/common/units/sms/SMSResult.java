package com.huanhong.common.units.sms;

import lombok.Data;

/**
 * 阿里云短息返回对象
 *
 * @author ldy81
 * @date 2019/12/12 15:51
 */
@Data
public class SMSResult {

    // {"Message":"OK","RequestId":"6BFAB704-29BF-490E-9D08-188907A34B62","BizId":"228212876136221121^0","Code":"OK"}
    // {"Message":"签名不合法(不存在或被拉黑)","RequestId":"B64300C7-5299-4094-AF8B-847CE4B5CE8E","Code":"isv.SMS_SIGNATURE_ILLEGAL"}

    private String Message;
    private String RequestId;
    private String BizId;
    private String Code;


}
