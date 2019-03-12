package project.finalyearapp.ViewHolder;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import project.finalyearapp.Interface.ItemClickListener;
import project.finalyearapp.R;


public class TransRequestHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtTransactionA;
    public TextView txtTransactionC;
    public TextView txtTransactionR;
    //public Button approveBtn;

    private ItemClickListener itemClickListener;

    public TransRequestHolder(View itemView) {
        super(itemView);

        txtTransactionA = (TextView) itemView.findViewById(R.id.trans_amount);
        txtTransactionC = (TextView) itemView.findViewById(R.id.trans_currency);
        txtTransactionR = (TextView) itemView.findViewById(R.id.trans_receiver);
        //approveBtn = (Button) itemView.findViewById(R.id.btnApprove);

        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view, getAdapterPosition(),false);
    }
}
