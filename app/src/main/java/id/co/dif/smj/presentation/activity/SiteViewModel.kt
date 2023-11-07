package id.co.dif.smj.presentation.activity

import androidx.lifecycle.MutableLiveData
import id.co.dif.smj.base.BaseResponse
import id.co.dif.smj.base.BaseViewModel
import id.co.dif.smj.data.TicketDetails

class SiteViewModel: BaseViewModel() {


    var responseDetilTicket = MutableLiveData<BaseResponse<TicketDetails>>()

}

