package com.tr.hsyn.telefonrehberi.xyz.main.color;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tr.hsyn.telefonrehberi.R;
import com.tr.hsyn.telefonrehberi.util.Dialog;
import com.tr.hsyn.telefonrehberi.util.concurrent.Worker;
import com.tr.hsyn.telefonrehberi.util.u;
import com.tr.hsyn.telefonrehberi.xyz.main.listener.ItemSelectListener;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("ConstantConditions")
public class SelectColorDialog implements ItemSelectListener{
   
   public static final int DEFAULT     = 0;
   public static final int PURPLE      = 1;
   public static final int BLUE        = 2;
   public static final int ORANGE      = 3;
   public static final int GREEN       = 4;
   public static final int BROWN       = 5;
   public static final int GREY        = 6;
   public static final int TEAL        = 7;
   public static final int PINK        = 8;
   public static final int LIME        = 9;
   public static final int AMBER       = 10;
   public static final int LIGHT_GREEN = 11;
   public static final int CHOCOLATE   = 12;
   public static final int SKY         = 13;
   public static final int NIGHT       = 14;
   public static final int KESTANE     = 15;
   public static final int ORKIDE      = 16;
   public static final int YONCA       = 17;
   public static final int ALEV       = 18;
   public static final int HAVUC       = 19;
   
   private List<ColorModel>        colors = new ArrayList<>();
   private String[]                colorNames;
   private int[]                   colorValues;
   private RecyclerView            recyclerView;
   private int                     selectedIndex;
   private WeakReference<Activity> activityWeakReference;
   private AlertDialog             dialog;
   
   private ItemSelectListener itemSelectListener;
   
   public SelectColorDialog(Activity activity){
      
      activityWeakReference = new WeakReference<>(activity);
      
      colorNames = activity.getResources().getStringArray(R.array.colors);
      setClors();
      generateColorModels();
      
      setupView();
   }
   
   @SuppressLint("InflateParams")
   private void setupView(){
      
      Activity       activity       = activityWeakReference.get();
      LayoutInflater layoutInflater = activity.getLayoutInflater();
      
      View view = layoutInflater.inflate(R.layout.dialog_change_color, null, false);
      
      recyclerView = view.findViewById(R.id.recyclerView);
      recyclerView.setLayoutManager(new LinearLayoutManager(activity));
      recyclerView.setAdapter(new SelectColorAdapter(colors).setClickListener(this, selectedIndex));
      
      AlertDialog.Builder builder = Dialog.alertBuilder(activity);
      builder.setCancelable(true);
      builder.setView(view);
      
      dialog = builder.create();
   
      dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationBounce;
      
      dialog.show();
   }
   
   public SelectColorDialog setSelectedIndex(int index){
      
      this.selectedIndex = index;
      ((SelectColorAdapter) recyclerView.getAdapter()).setSelectedIndex(index);
      
      return this;
   }
   
   private void generateColorModels(){
      
      for(int i = 0; i < colorNames.length; i++){
         colors.add(new ColorModel(colorValues[i], colorNames[i]));
      }
   }
   
   private void setClors(){
      
      colorValues = new int[colorNames.length];
      
      Activity activity = activityWeakReference.get();
      
      
      colorValues[0]  = u.getColor(activity, R.color.colorPrimary);
      colorValues[1]  = u.getColor(activity, R.color.color_primary_purple);
      colorValues[2]  = u.getColor(activity, R.color.color_primary_blue);
      colorValues[3]  = u.getColor(activity, R.color.color_primary_orange);
      colorValues[4]  = u.getColor(activity, R.color.color_primary_green);
      colorValues[5]  = u.getColor(activity, R.color.color_primary_brown);
      colorValues[6]  = u.getColor(activity, R.color.color_primary_grey);
      colorValues[7]  = u.getColor(activity, R.color.color_primary_teal);
      colorValues[8]  = u.getColor(activity, R.color.color_primary_pink);
      colorValues[9]  = u.getColor(activity, R.color.color_primary_lime);
      colorValues[10] = u.getColor(activity, R.color.color_primary_amber);
      colorValues[11] = u.getColor(activity, R.color.color_primary_light_green);
      colorValues[12] = u.getColor(activity, R.color.color_primary_çikolata);
      colorValues[13] = u.getColor(activity, R.color.color_primary_sky);
      colorValues[14] = u.getColor(activity, R.color.color_primary_night);
      colorValues[15] = u.getColor(activity, R.color.color_primary_kestane);
      colorValues[16] = u.getColor(activity, R.color.color_primary_orkide);
      colorValues[17] = u.getColor(activity, R.color.color_primary_yonca);
      colorValues[18] = u.getColor(activity, R.color.color_primary_alev);
      colorValues[19] = u.getColor(activity, R.color.color_primary_havuc);
   }
   
   public SelectColorDialog setItemSelectListener(ItemSelectListener itemSelectListener){
   
      this.itemSelectListener = itemSelectListener;
      return this;
   }
   
   @Override
   public void onItemSelected(int position){
      
      Worker.onMain(this::close, 600);
      
      if(itemSelectListener != null) itemSelectListener.onItemSelected(position);
   }
   
   private void close(){
      
      dialog.dismiss();
   }
   
}
