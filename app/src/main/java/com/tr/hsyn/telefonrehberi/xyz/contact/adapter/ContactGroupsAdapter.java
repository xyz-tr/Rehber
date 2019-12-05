package com.tr.hsyn.telefonrehberi.xyz.contact.adapter;


import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.github.florent37.viewanimator.ViewAnimator;
import com.tr.hsyn.telefonrehberi.R;
import com.tr.hsyn.telefonrehberi.util.Bungee;
import com.tr.hsyn.telefonrehberi.util.concurrent.Worker;
import com.tr.hsyn.telefonrehberi.util.ui.snack.Show;
import com.tr.hsyn.telefonrehberi.xyz.contact.Account;
import com.tr.hsyn.telefonrehberi.xyz.contact.Contacts;
import com.tr.hsyn.telefonrehberi.xyz.contact.Group;
import com.tr.hsyn.telefonrehberi.xyz.contact.activity.group.GroupActivity;
import com.tr.hsyn.telefonrehberi.xyz.contact.activity.group.GroupItemLongClickListener;
import com.tr.hsyn.telefonrehberi.xyz.contact.activity.group.LabeledContactsActivity;
import com.tr.hsyn.telefonrehberi.xyz.main.MainActivity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;


public class ContactGroupsAdapter extends RecyclerView.Adapter<ContactGroupsAdapter.AccountViewHolder>{
   
   private       Account[]                  accounts;
   private final GroupActivity              activity;
   private       GroupItemLongClickListener longClickListener;
   private       ViewGroup[]                groupLayouts;
   private       Map<Group, ViewGroup>      groupViewMap = new HashMap<>();

   
   public ContactGroupsAdapter(final GroupActivity activity, Account[] accounts){
      
      this.accounts = accounts;
      this.activity = activity;
      groupLayouts  = new ViewGroup[accounts.length];
   }
   
   public ViewGroup[] getGroupLayouts(){
      
      return groupLayouts;
   }
   
   public ContactGroupsAdapter setLongClickListener(GroupItemLongClickListener listener){
      
      longClickListener = listener;
      return this;
   }
   
   public void updateGroupCount(Group group){
      
      ViewGroup groupView = groupViewMap.get(group);
      
      if(groupView != null){
         
         TextView countTextView = groupView.findViewById(R.id.groupCount);
         
         ViewCompat.animate(countTextView)
                   .alpha(0)
                   .rotationY(360)
                   .setDuration(600)
                   .withEndAction(() -> {
                  
                  countTextView.setText(String.valueOf(group.getMembersCount()));
                  
                  ViewCompat.animate(countTextView)
                        .alpha(1)
                        .setDuration(600)
                        .start();
               })
                   .start();
         
         
      }
      else{
   
         Timber.w("Güncellenecek grup bulunamadı");
      }
      
   }
   
   @NonNull
   @Override
   public AccountViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i){
      
      View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.group_title_item, viewGroup, false);
      
      return new AccountViewHolder(view);
   }
   
   @Override
   public void onBindViewHolder(@NonNull AccountViewHolder holder, int i){
      
      Account account = accounts[i];
      
      holder.title.setText(account.name);
      
   }
   
   @Override
   public int getItemCount(){
      
      return accounts.length;
   }
   
   class AccountViewHolder extends
         RecyclerView.ViewHolder implements
         CompoundButton.OnCheckedChangeListener{
      
      TextView  title;
      ImageView icon;
      ViewGroup itemView;
      private boolean   click;
      private ViewGroup groupsLayout;
      
      
      AccountViewHolder(View itemView){
         
         super(itemView);
         
         this.itemView = (ViewGroup) itemView;
         title         = itemView.findViewById(R.id.accountName);
         icon          = itemView.findViewById(R.id.icon);
         
         View main = itemView.findViewById(R.id.mainRelativeLayout);
         
         groupsLayout = itemView.findViewById(R.id.groupsLayout);
         
         main.setOnClickListener((v) -> onClickAccount(v, accounts[getAdapterPosition()], getAdapterPosition()));
         main.setBackgroundResource(MainActivity.getWellRipple());
         
      }
      
      private void animateIcon(boolean rotate, ViewGroup layout){
         
         if(rotate){
            
            icon.animate().rotation(90).start();
   
            ViewAnimator.animate(layout)
                        .pivotY(0)
                        .alpha(0,1)
                        .scaleY(0,1)
                        .accelerate()
                        .duration(150)
                        .start();
                        
         }
         else{
            icon.animate().rotation(0).start();
   
            /*ViewAnimator.animate(layout)
                        .pivotX(0)
                        .pivotY(0)
                        .alpha(1,0)
                        .fadeIn()
                        .decelerate()
                        .duration(600)
                        .start();*/
         }
      }
      
      void onClickAccount(View v, Account account, int index){
         
         if(groupLayouts[index] == null){
            
            groupLayouts[index] = groupsLayout;
         }
         
         if(click){
            
            click = false;
            groupsLayout.removeAllViews();
            animateIcon(false, groupsLayout);
            return;
         }
         
         click = true;
         animateIcon(true, groupsLayout);
         
         List<Group> groups = Contacts.getGroupsInfo(activity, account);
         
         if(groups == null){ return; }
         
         for(Group group : groups){
            
            ViewGroup groupItem = (ViewGroup) activity.getLayoutInflater().inflate(
                  R.layout.group_item,
                  null,
                  false);
            
            groupViewMap.put(group, groupItem);
            
            ImageView groupIcon = groupItem.findViewById(R.id.icon);
            TextView  groupName = groupItem.findViewById(R.id.groupName);
            
            
            String title = group.getTitle();
            
            switch(title){
               
               case "Friends":
                  groupIcon.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.group_friends));
                  break;
               
               case "My Contacts":
                  groupIcon.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.groups_mycontacts));
                  break;
               
               case "Family":
                  groupIcon.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.group_family));
                  break;
               
               case "Coworkers":
                  groupIcon.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.groups_coworkers));
                  break;
               
               case "Starred in Android":
                  groupIcon.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.groups_favorite));
                  break;
               
               default:
                  groupIcon.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.groups_else));
                  break;
            }
            
            groupName.setText(GroupActivity.getTranslatedGroupName(activity, title));
            
            groupsLayout.addView(groupItem);
            
            Switch groupSwitch = groupItem.findViewById(R.id.groupSwitch);
            groupSwitch.setChecked(group.isVisable());
            groupItem.setBackgroundResource(MainActivity.getWellRipple());
            
            groupSwitch.setOnCheckedChangeListener(this);
            groupSwitch.setTag(group);
            groupItem.setTag(group);
            groupItem.setOnClickListener(this::onClickGroupItem);
            groupItem.setOnLongClickListener((vv) -> onLongClickGroupItem(vv, index));
            TextView groupCount = groupItem.findViewById(R.id.groupCount);
            
            if(group.getMembersCount() >= 0){
               
               groupCount.setText(String.valueOf(group.getMembersCount()));
               continue;
            }
   
            Worker.onBackground(() -> {
               
               final int count = Contacts.getGroupCount(activity, group);
               
               activity.runOnUiThread(() -> {
                  
                  groupCount.setText(String.valueOf(count));
                  groupCount.setVisibility(View.VISIBLE);
               });
            }, "ContactGroupsAdapter:Grupta kaç kişi olduğunu bulma");
         }
      }
      
      private boolean onLongClickGroupItem(View v, int index){
         
         Group group = (Group) v.getTag();
         
         if(longClickListener != null){ longClickListener.onLongClickGroupItem(v, group, index); }
         
         return true;
      }
      
      void onClickGroupItem(View v){
         
         Group  group = (Group) v.getTag();
         Intent i     = new Intent(activity, LabeledContactsActivity.class);
         
         i.putExtra(LabeledContactsActivity.EXTRA_LABEL, group);
         
         
         activity.startActivityForResult(i, 11);
         Bungee.slideUp(activity);
      }
      
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
         
         Group group = (Group) buttonView.getTag();
         
         if(isChecked){
            
            if(group.isVisable()){ return; }
            if(Contacts.setGroupVisibility(buttonView.getContext(), group.getId(), "1")){
               
               group.setVisable(true);
               Show.globalMessage(activity, activity.getString(R.string.msg_group_visable, GroupActivity.getTranslatedGroupName(activity, group.getTitle())));
            }
         }
         else{
            
            if(!group.isVisable()){ return; }
            if(Contacts.setGroupVisibility(buttonView.getContext(), group.getId(), "0")){
               
               group.setVisable(false);
               Show.globalMessage(activity, activity.getString(R.string.msg_group_invisable, GroupActivity.getTranslatedGroupName(activity, group.getTitle())));
            }
         }
         
      }
      
      
   }
   
   
}
