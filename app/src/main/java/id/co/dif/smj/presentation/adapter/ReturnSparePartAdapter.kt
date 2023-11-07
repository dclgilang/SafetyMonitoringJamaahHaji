package id.co.dif.smj.presentation.adapter

import id.co.dif.smj.R
import id.co.dif.smj.base.BaseAdapter
import id.co.dif.smj.base.BaseViewModel
import id.co.dif.smj.data.Note
import id.co.dif.smj.databinding.ItemReturnSparePartBinding


class ReturnSparePartAdapter() : BaseAdapter<BaseViewModel, ItemReturnSparePartBinding, Note>() {



    override val layoutResId = R.layout.item_return_spare_part

    override fun onLoadItem(
        binding: ItemReturnSparePartBinding,
        data: MutableList<Note>,
        position: Int
    )  {
//        val item = data[position]
//        binding.idLastUpdate.text = item.time
//        binding.idFollowUp.text = item.date
//        binding.idTicket.text = item.tic_status
//        binding.idCreated.text = item.username
//        binding.idArticleName.text = item.username
//        binding.idArticleCode.text = item.note
//        binding.idSerialNumber.text = item.time
//        binding.idQuantity.text = item.tic_status
//        binding.idCity.text = item.username
//        binding.idPic.text = item.username
//        binding.idRequest.text = item.note
//        binding.idSystem.text = item.username
//        binding.idVendor.text = item.username
//        binding.idTreatment.text = item.note


    }

    override fun getItemCount(): Int {
        return 5
    }



}