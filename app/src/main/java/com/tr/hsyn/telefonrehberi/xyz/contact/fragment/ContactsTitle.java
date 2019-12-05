package com.tr.hsyn.telefonrehberi.xyz.contact.fragment;

import com.tr.hsyn.telefonrehberi.R;
import com.tr.hsyn.telefonrehberi.util.observable.Observe;
import com.tr.hsyn.telefonrehberi.xyz.contact.Account;
import com.tr.hsyn.telefonrehberi.xyz.contact.IMainContact;
import com.tr.hsyn.telefonrehberi.xyz.main.controller.HaveTitle;
import com.tr.hsyn.telefonrehberi.xyz.main.controller.TitleController;

import java.util.List;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.val;


public abstract class ContactsTitle extends ContactsView implements HaveTitle{
   
   @Getter(AccessLevel.PROTECTED) private final Observe<String> title = new Observe<>("Rehber");
   @Getter(AccessLevel.PROTECTED) private final Observe<String> subTitle = new Observe<>("");
   
   public void setTitleController(TitleController titleController){
      
      if(titleController != null) {
   
         title.setChangeOnSameValue(true);
         subTitle.setChangeOnSameValue(true);
         title.setObserver(t -> setTitle(titleController, t));
         subTitle.setObserver(st -> setSubTitle(titleController, st));
         titleController.setTitle(title.getValue(), subTitle.getValue());
      }
   }
   
   protected void setTitle(){
      
      if(getActivity() == null) return;
      
      String title;
      
      if(getSelectedAccount() != null){
         
         val accountName = getSelectedAccount().name;
   
         if(accountName.contains("@")){
   
            title = accountName.substring(0, accountName.indexOf('@'));
         }
         else{
            
            title = accountName;
         }
      }
      else{
         
         title = getString(R.string.app_name);
      }
      
      this.title.setValue(title);
      
      if(getAdapterContacts() != null)
         this.subTitle.setValue(String.valueOf(getAdapterContacts().size()));
   }
   
   /**
    * Uygulama açıldığında varsayılan olarak herhangi bir hesap seçilmiş değildir.
    * Ve yine varsayılan olarak, bir hesap seçildiğinde bu, uygulama açık olduğu sürece geçerli olacak bir değişkende tutulur.
    * Yani bir hesap seçilse bile uygulama kapanıp açıldığında yine bir hesap seçili değildir.
    * Seçilen hesabın hatırlanması istenirse ancak bu hesap sürekli hatırlanır.
    * Bu metod, eğer bir hesap seçilmişse, hatırlanması istensin yada istenmesin, uygulama açık olduğu sürece bu geçerli olan hesabı döndürür.
    *
    * @return {@link Account}
    */
   protected abstract Account getSelectedAccount();
   
   protected abstract List<IMainContact> getAdapterContacts();
   
   
   
}
