package com.huanhong.wms.dto.request;

import com.huanhong.wms.entity.TemporaryLibraryInventory;
import com.huanhong.wms.entity.TemporaryLibraryInventoryDetails;
import lombok.Data;

import java.util.List;

/**
 * @Author wang
 * @date 2022/5/26 11:30
 */
@Data
public class TemporaryLibraryInventoryRequest {

    TemporaryLibraryInventory temporaryLibraryInventory;

    List<TemporaryLibraryInventoryDetails> temporaryLibraryInventoryDetails;
}
