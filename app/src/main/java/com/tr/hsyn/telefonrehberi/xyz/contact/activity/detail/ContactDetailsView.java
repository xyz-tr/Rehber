package com.tr.hsyn.telefonrehberi.xyz.contact.activity.detail;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.simmorsal.recolor_project.ReColor;
import com.tr.hsyn.telefonrehberi.R;
import com.tr.hsyn.telefonrehberi.util.u;

import butterknife.BindView;
import butterknife.ButterKnife;
import lombok.Getter;
import lombok.val;


public abstract class ContactDetailsView extends AppCompatActivity{
   
   @Getter private                   int                     primaryColor;
   @BindView(R.id.actionButton)      FloatingActionButton    actionButton;
   @BindView(R.id.image)             ImageView               image;
   @BindView(R.id.collapsingToolbar) CollapsingToolbarLayout collapsingToolbarLayout;
   
   @Override
   protected void onCreate(@Nullable Bundle savedInstanceState){
      
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_contact_details);
      ButterKnife.bind(this);
      
      primaryColor = u.getPrimaryColor(this);
      
      findViewById(R.id.coordinator).setBackgroundColor(u.lighter(primaryColor, .91F));
      val font = ResourcesCompat.getFont(this, R.font.text);
      
      collapsingToolbarLayout.setCollapsedTitleTypeface(font);
      collapsingToolbarLayout.setExpandedTitleTypeface(font);
      
      Toolbar toolbar = findViewById(R.id.toolbar);
      setSupportActionBar(toolbar);
      
      toolbar.setNavigationOnClickListener(v -> onBackPressed());
      
      new ReColor(this).setStatusBarColor(null, u.colorToString(u.darken(primaryColor, .9F)), 600);
   }
}
