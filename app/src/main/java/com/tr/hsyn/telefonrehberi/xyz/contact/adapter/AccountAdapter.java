package com.tr.hsyn.telefonrehberi.xyz.contact.adapter;

import android.accounts.Account;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tr.hsyn.telefonrehberi.R;
import com.tr.hsyn.telefonrehberi.util.u;
import com.tr.hsyn.telefonrehberi.xyz.contact.activity.account.AccountModel;
import com.tr.hsyn.telefonrehberi.xyz.main.MainActivity;
import com.tr.hsyn.telefonrehberi.xyz.main.listener.ItemSelectListener;

import java.util.List;


public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.AccountViewHolder>{
   
   private List<AccountModel> accounts;
   private ItemSelectListener clickListener;
   private Account            selectedAccount;
   
   public AccountAdapter(List<AccountModel> accounts, ItemSelectListener clickListener, Account selectedAccount){
      
      this.accounts        = accounts;
      this.clickListener   = clickListener;
      this.selectedAccount = selectedAccount;
   }
   
   
   @NonNull
   @Override
   public AccountViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i){
      
      return new AccountViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.account_item, viewGroup, false));
   }
   
   @Override
   public void onBindViewHolder(@NonNull AccountViewHolder viewHolder, int i){
      
      AccountModel account = accounts.get(i);
      
      viewHolder.accountName.setText(account.name);
      viewHolder.accountType.setText(account.type);
      viewHolder.contactCount.setText(String.valueOf(account.count));
      
      
      if(selectedAccount != null){
         
         if(account.name.equals(selectedAccount.name) && account.type.equals(selectedAccount.type)){
            
            viewHolder.selected.setVisibility(View.VISIBLE);
         }
         else{
            viewHolder.selected.setVisibility(View.INVISIBLE);
         }
      }
      else{
         
         if(account.name.equals(viewHolder.itemView.getContext().getString(R.string.all_contacts))){
            
            viewHolder.selected.setVisibility(View.VISIBLE);
         }
      }
   }
   
   @Override
   public void onViewRecycled(@NonNull AccountViewHolder holder){
      
      super.onViewRecycled(holder);
      
      Account account = accounts.get(holder.getAdapterPosition());
      
      if(selectedAccount != null){
         
         if(account.name.equals(selectedAccount.name) && account.type.equals(selectedAccount.type)){
            
            holder.selected.setVisibility(View.VISIBLE);
         }
         else{
            holder.selected.setVisibility(View.INVISIBLE);
         }
      }
      else{
         
         if(accounts.get(holder.getAdapterPosition()).name.equals(holder.itemView.getContext().getString(R.string.all_contacts))){
            
            holder.selected.setVisibility(View.VISIBLE);
         }
      }
   }
   
   @Override
   public int getItemCount(){
      
      return accounts.size();
   }
   
   final class AccountViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
      
      final TextView  accountName;
      final TextView  accountType;
      final TextView  contactCount;
      final ImageView selected;
      
      AccountViewHolder(@NonNull View itemView){
         
         super(itemView);
         
         accountName  = itemView.findViewById(R.id.accountName);
         accountType  = itemView.findViewById(R.id.accountType);
         contactCount = itemView.findViewById(R.id.contactCount);
         selected     = itemView.findViewById(R.id.seleted);
         
         View root = itemView.findViewById(R.id.mainRelativeLayout);
         
         u.setTintDrawable(selected.getDrawable(), u.getPrimaryColor(itemView.getContext()));
         
         root.setBackgroundResource(MainActivity.getWellRipple());
         root.setOnClickListener(this);
      }
      
      @Override
      public void onClick(View v){
         
         if(clickListener != null){
            clickListener.onItemSelected(getAdapterPosition());
         }
      }
   }
   
   
}
