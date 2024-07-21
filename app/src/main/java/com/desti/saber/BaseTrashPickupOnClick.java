package com.desti.saber;

import android.view.View;
import com.desti.saber.LayoutHelper.PickupTrashes.PickupTrashesList;
import com.desti.saber.utils.dto.PickupDetailDto;

public interface BaseTrashPickupOnClick {
    void onClickPickupBtn(View v, PickupTrashesList pickupTrashesList, PickupDetailDto pickupDetailDto);
    void pickupPopUpList(PickupTrashesList pickupTrashesList);
}
