package com.huanhong.wms.entity.vo;

import com.huanhong.wms.entity.OutboundRecordDetailsVO;
import com.huanhong.wms.entity.dto.UpdatePlanUseOutDetailsDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import java.util.List;


@Data
@ApiModel(value = "PDA-更新领料出库明细及其出库记录")
public class PdaUpdateOutVO {

    @Valid
    @ApiModelProperty("明细信息")
    private UpdatePlanUseOutDetailsDTO updatePlanUseOutDetailsDTO;

    @Valid
    @ApiModelProperty("出库记录")
    private List<OutboundRecordDetailsVO> outboundRecordDetailsVOList;

}
