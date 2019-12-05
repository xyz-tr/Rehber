package com.tr.hsyn.telefonrehberi.xyz.contact.activity.detail;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;

import com.annimon.stream.Stream;
import com.github.florent37.viewanimator.ViewAnimator;
import com.tr.hsyn.telefonrehberi.R;
import com.tr.hsyn.telefonrehberi.util.concurrent.Worker;
import com.tr.hsyn.telefonrehberi.util.event.DeleteCall;
import com.tr.hsyn.telefonrehberi.util.phone.Phone;
import com.tr.hsyn.telefonrehberi.util.u;
import com.tr.hsyn.telefonrehberi.xyz.call.ICall;
import com.tr.hsyn.telefonrehberi.xyz.call.Type;
import com.tr.hsyn.telefonrehberi.xyz.call.history.Historicall;
import com.tr.hsyn.telefonrehberi.xyz.call.main.fragment.Kickback_CallsConst;
import com.tr.hsyn.telefonrehberi.xyz.call.story.CallStory;
import com.tr.hsyn.telefonrehberi.xyz.contact.IPhoneContact;
import com.tr.hsyn.telefonrehberi.xyz.main.Kickback_MainBox;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;


public abstract class ContactDetailsCallSummary extends ContactDetailsUpdate{
   
   private              int                 rippleBackground;
   private              boolean             showSummary;
   private              boolean             permissionsGrant;
   final                String[]            CALL_PERMISSIONS = {
         
         Manifest.permission.READ_CALL_LOG,
         Manifest.permission.WRITE_CALL_LOG
   };
   private final        Kickback_CallsConst callsConst       = Kickback_CallsConst.getInstance();
   private static final int                 RC_CALL_SUMMARY  = 0b10011;
   
   @Override
   protected void onCreate(Bundle bundle){
      
      super.onCreate(bundle);
      
      if(contact.getNumber() == null){
         
         return;
      }
      
      rippleBackground = Kickback_MainBox.getInstance().getWellRipple();
      
      if(Phone.isPermissionsGrant(this, Manifest.permission.WRITE_CALL_LOG)){
         
         CallStory callStory = callsConst.getCallStory();
         
         if(callStory != null && callStory.getCalls() != null){
            
            setupSummary(callStory);
         }
      }
      else{
         
         View call_summary_header = findViewById(R.id.call_summary_header_container);
         call_summary_header.setVisibility(View.VISIBLE);
         View innerHead = call_summary_header.findViewById(R.id.call_summary_header);
         innerHead.setBackgroundResource(rippleBackground);
         innerHead.setOnClickListener(v -> {
            
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
               
               requestPermissions(CALL_PERMISSIONS, RC_CALL_SUMMARY);
            }
         });
      }
   }
   
   /**
    * Kişinin arama özetini hazırla.
    * Arama özeti kişinin gelen, giden, cevapsız, reddedilen ve engellenen çağrılarından oluşuyor.
    * Konuşma olan gelen ve giden aramalar için toplam konuşma süresi gösterilecek.
    * Ve en sonda da bunların da toplamı gösterilecek
    *
    * @param callStory Arama kayıtları
    */
   private void setupSummary(CallStory callStory){
      
      View call_summary_header = findViewById(R.id.call_summary_header_container);
      call_summary_header.setVisibility(View.VISIBLE);
      View innerHead = call_summary_header.findViewById(R.id.call_summary_header);
      innerHead.setBackgroundResource(rippleBackground);
      
      final ViewGroup callSummaryLayout = findViewById(R.id.callSummaryMain);
      View            summaryView       = getLayoutInflater().inflate(R.layout.call_details_summary, callSummaryLayout, false);
      
      final TextView incommingCallSize     = summaryView.findViewById(R.id.incomming_call);
      final TextView outgoingCallSize      = summaryView.findViewById(R.id.outgoing_call);
      final TextView missedCallSize        = summaryView.findViewById(R.id.missed_call);
      final TextView rejectedCallSize      = summaryView.findViewById(R.id.rejected_call);
      final TextView blockedCallSize       = summaryView.findViewById(R.id.blocked_call);
      final TextView totalCallSize         = summaryView.findViewById(R.id.total_call);
      final TextView incommingCallDuration = summaryView.findViewById(R.id.incomming_call_duration);
      final TextView outgoingCallDuration  = summaryView.findViewById(R.id.outgoing_call_duration);
      final TextView totalCallDuration     = summaryView.findViewById(R.id.total_call_duration);
      
      Worker.onBackground(() -> {
         
         Historicall<IPhoneContact> historicall = callStory.createHistory(contact);
         
         if(historicall.getCalls().isEmpty()){
            
            Worker.onMain(() -> call_summary_header.setVisibility(View.GONE));
            return;
         }
         
         final List<ICall> incommingCalls = historicall.getIncommingCalls();
         final List<ICall> outgoingCalls  = historicall.getOutgoingCalls();
         final List<ICall> missedCalls    = historicall.getMissedCalls();
         final List<ICall> rejectedCalls  = historicall.getRejectedCalls();
         final List<ICall> blockedCalls   = historicall.getBlockedCalls();
         
         final int[] incommingDuration = {0};
         final int[] outgoingDuration  = {0};
         int         totalDuration;
         int         totalCalls        = incommingCalls.size() + outgoingCalls.size() + missedCalls.size() + rejectedCalls.size() + blockedCalls.size();
         
         Stream.of(incommingCalls).forEach(cx -> incommingDuration[0] += cx.getDuration());
         Stream.of(outgoingCalls).forEach(cx -> outgoingDuration[0] += cx.getDuration());
         
         totalDuration = incommingDuration[0] + outgoingDuration[0];
         
         String iVal = getString(R.string.val, incommingCalls.size());
         String oVal = getString(R.string.val, outgoingCalls.size());
         String mVal = getString(R.string.val, missedCalls.size());
         String rVal = getString(R.string.val, rejectedCalls.size());
         String bVal = getString(R.string.val, blockedCalls.size());
         String tVal = getString(R.string.val, totalCalls);
         
         String iDuration = u.formatSeconds(incommingDuration[0]);
         String oDuration = u.formatSeconds(outgoingDuration[0]);
         String tDuration = u.formatSeconds(totalDuration);
         
         
         Worker.onMain(() -> {
            
            incommingCallSize.setText(iVal);
            outgoingCallSize.setText(oVal);
            missedCallSize.setText(mVal);
            rejectedCallSize.setText(rVal);
            blockedCallSize.setText(bVal);
            
            incommingCallDuration.setText(iDuration);
            outgoingCallDuration.setText(oDuration);
            totalCallDuration.setText(tDuration);
            totalCallSize.setText(tVal);
            
            callSummaryLayout.addView(summaryView);
            
            summaryView.findViewById(R.id.incommingCalls).setOnClickListener((v) -> onClickSummary(Type.INCOMMING));
            summaryView.findViewById(R.id.outgoingPhoneRecords).setOnClickListener((v) -> onClickSummary(Type.OUTGOING));
            summaryView.findViewById(R.id.missedCalls).setOnClickListener((v) -> onClickSummary(Type.MISSED));
            summaryView.findViewById(R.id.rejectedPhoneRecords).setOnClickListener((v) -> onClickSummary(Type.REJECTED));
            summaryView.findViewById(R.id.blockedCalls).setOnClickListener((v) -> onClickSummary(Type.BLOCKED));
            summaryView.findViewById(R.id.allPhoneRecords).setOnClickListener((v) -> onClickSummary(Type.UNKNOWN));
            
            
            ImageView summaryIcon      = findViewById(R.id.call_summary_icon);
            ImageView expand_indicator = findViewById(R.id.expand_indicator);
            
            int color = u.getPrimaryColor(this);
            color = u.lighter(color, 0.3F);
            
            u.setTintDrawable(summaryIcon.getDrawable(), color);
            u.setTintDrawable(expand_indicator.getDrawable(), color);
            
            
            innerHead.setOnClickListener(v -> onClickCallSummary(v, call_summary_header, callSummaryLayout, expand_indicator));
            
            if(showSummary) innerHead.performClick();
            
         });
      }, "ContactDetailsCallSummary:Kişinin arama özetlerinin hazırlanması - [" + contact.getName() + "]");
   }
   
   private void onClickCallSummary(View clickedView, View header, View content, View indicator){
      
      clickedView.setOnClickListener(null);
      NestedScrollView scrollView = findViewById(R.id.nestedScrollView);
      
      LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) header.getLayoutParams();
      
      params.bottomMargin = 0;
      header.setLayoutParams(params);
      
      content.setVisibility(View.VISIBLE);
      content.setAlpha(0);
      header.setTranslationZ(1);
      
      content.animate().alpha(1).scaleY(1).setDuration(600L).setInterpolator(new AccelerateInterpolator()).start();
      
      //ViewAnimator.animate(content).scaleY(1).duration(500L).accelerate().start();
      
      ViewAnimator.animate(indicator)
                  .rotation(180)
                  .duration(500)
                  .accelerate()
                  .thenAnimate(indicator)
                  .fadeOut()
                  .start();
      
      Worker.onMain(() -> scrollView.fullScroll(ScrollView.FOCUS_DOWN), 200);
      
   }
   
   /**
    * Arama özetine tıklanmıştır.
    *
    * @param type Tıklanan tür, gelen giden vs.
    */
   private void onClickSummary(int type){
      
      openHistory(type);
   }
   
   @Override
   public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
      
      super.onRequestPermissionsResult(requestCode, permissions, grantResults);
      EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, ContactDetailsCallSummary.this);
   }
   
   @Subscribe
   public void onDeleteCallFromHistory(DeleteCall event){
      
      //final ViewGroup root              = findViewById(R.id.detailsRoot);
      final ViewGroup callSummaryLayout = findViewById(R.id.callSummaryMain);
      
      callSummaryLayout.removeAllViews();
      
      CallStory callStory = Kickback_CallsConst.getInstance().getCallStory();
      
      if(callStory == null){
         
         return;
      }
      
      setupSummary(callStory);
      ViewGroup contactHistoryLayout = findViewById(R.id.contact_history_item);
      
      Worker.onBackground(() -> {
         
         assert contact.getNumberList() != null;
         
         if(callStory.getCalls(contact.getNumberList()).size() == 0){
            
            runOnUiThread(() -> {
               
               TransitionManager.beginDelayedTransition(contactHistoryLayout);
               ((TextView) contactHistoryLayout.findViewById(R.id.text)).setText(getString(R.string.no_history));
            });
         }
         
      }, "ContactDetailsActivity:Rehber kaydına ait bir arama silindiğinde başka bir arama kaydı olup olmadığını kontrol etme");
      
   }
   
   @AfterPermissionGranted(RC_CALL_SUMMARY)
   private void onCallSummaryGrant(){
      
      if(permissionsGrant) return;
      
      permissionsGrant = true;
      
      Worker.onBackground(this::getCalls, "ContactDetailsCallSummary:Arama özeti için arama kayıtlarını alma")
            .whenCompleteAsync(this::showSummary, Worker.getMainThreadExecutor());
   }
   
   private List<ICall> getCalls(){
      
      return CallStory.getSystemCallLogCalls(getContentResolver());
   }
   
   private void showSummary(List<ICall> calls, Throwable throwable){
      
      callsConst.setCallStory(CallStory.createStory(calls));
      showSummary = true;
      setupSummary(callsConst.getCallStory());
   }
}
