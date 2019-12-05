package com.tr.hsyn.telefonrehberi.util.spanner;

import android.app.PendingIntent;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.EmbossMaskFilter;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.LocaleList;
import android.text.Layout;
import android.text.style.BulletSpan;
import android.text.style.ClickableSpan;
import android.text.style.EasyEditSpan;
import android.text.style.ImageSpan;
import android.text.style.LocaleSpan;
import android.text.style.MaskFilterSpan;
import android.text.style.QuoteSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.ScaleXSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.SubscriptSpan;
import android.text.style.SuggestionSpan;
import android.text.style.SuperscriptSpan;
import android.text.style.TabStopSpan;
import android.text.style.TextAppearanceSpan;
import android.text.style.TypefaceSpan;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.Dimension;
import androidx.annotation.DrawableRes;
import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.Locale;


public class Spans{
   
   private Spans(){
      //hide
   }
   
   /**
    * @see android.text.style.AbsoluteSizeSpan#AbsoluteSizeSpan(int)
    */
   public static Span sizePX(@Dimension(unit = Dimension.PX) final int px){
      
      return new Span(new AbsoluteSizeSpanBuilder(px, false));
   }
   
   /**
    * @see android.text.style.AbsoluteSizeSpan#AbsoluteSizeSpan(int, boolean)
    */
   public static Span sizeDP(@Dimension(unit = Dimension.DP) final int dp){
      
      return new Span(new AbsoluteSizeSpanBuilder(dp, true));
   }
   
   /**
    * @see android.text.style.AbsoluteSizeSpan#AbsoluteSizeSpan(int, boolean)
    */
   public static Span sizeSP(@Dimension(unit = Dimension.SP) final int sp){
      
      return new Span(new AbsoluteSizeSpanBuilder(sp, true));
   }
   
   /**
    * @see RelativeSizeSpan#RelativeSizeSpan(float)
    */
   public static Span scaleSize(@FloatRange(from = 0.0) final float proportion){
      
      return new Span(() -> new RelativeSizeSpan(proportion));
   }
   
   /**
    * @see ScaleXSpan#ScaleXSpan(float)
    */
   public static Span scaleXSize(@FloatRange(from = 0.0) final float proportion){
      
      return new Span(() -> new ScaleXSpan(proportion));
   }
   
   /**
    * @see android.text.style.StyleSpan
    */
   public static Span bold(){
      
      return new Span(new StyleSpanBuilder(Typeface.BOLD));
   }
   
   /**
    * @see android.text.style.StyleSpan
    */
   public static Span italic(){
      
      return new Span(new StyleSpanBuilder(Typeface.ITALIC));
   }
   
   /**
    * @see android.text.style.StyleSpan
    */
   public static Span boldItalic(){
      
      return new Span(new StyleSpanBuilder(Typeface.BOLD_ITALIC));
   }
   
   /**
    * @see TypefaceSpan#TypefaceSpan(String)
    */
   public static Span font(@Nullable final String font){
      
      return new Span(() -> new TypefaceSpan(font));
   }
   
   /**
    * @see TypefaceSpan#TypefaceSpan(String)
    */
   @RequiresApi(api = Build.VERSION_CODES.P)
   public static Span font(@NonNull final Typeface font){
      
      return new Span(() -> new TypefaceSpan(font));
   }
   
   /**
    * @see StrikethroughSpan#StrikethroughSpan()
    */
   public static Span strikeThrough(){
      
      return new Span(StrikethroughSpan::new);
   }
   
   /**
    * @see UnderlineSpan#UnderlineSpan()
    */
   public static Span underline(){
      
      return new Span(UnderlineSpan::new);
   }
   
   /**
    * @see android.text.style.BackgroundColorSpan#BackgroundColorSpan(int)
    */
   public static Span background(@ColorInt final int color){
      
      return new Span(new ColorSpanBuilder(ColorSpanBuilder.BACKGROUND, color));
   }
   
   /**
    * @see android.text.style.ForegroundColorSpan#ForegroundColorSpan(int)
    */
   public static Span foreground(@ColorInt final int color){
      
      return new Span(new ColorSpanBuilder(ColorSpanBuilder.FOREGROUND, color));
   }
   
   /**
    * @see SubscriptSpan#SubscriptSpan()
    */
   public static Span subscript(){
      
      return new Span(SubscriptSpan::new);
   }
   
   /**
    * @see SuperscriptSpan#SuperscriptSpan()
    */
   public static Span superscript(){
      
      return new Span(SuperscriptSpan::new);
   }
   
   /**
    * The drawable must already have bounds ({@link Drawable#setBounds(Rect)})
    *
    * @see ImageSpan#ImageSpan(Drawable)
    */
   public static com.tr.hsyn.telefonrehberi.util.spanner.ImageSpan image(@NonNull final Drawable drawable){
      
      return new com.tr.hsyn.telefonrehberi.util.spanner.ImageSpan(new SpanBuilder(){
         
         @Override
         public Object build(){
            
            return new ImageSpan(drawable);
         }
      });
   }
   
   /**
    * The drawable must already have bounds ({@link Drawable#setBounds(Rect)})
    *
    * @see ImageSpan#ImageSpan(Drawable, int)
    */
   public static com.tr.hsyn.telefonrehberi.util.spanner.ImageSpan image(@NonNull final Drawable drawable, final int verticalAlignment){
      
      return new com.tr.hsyn.telefonrehberi.util.spanner.ImageSpan(new SpanBuilder(){
         
         @Override
         public Object build(){
            
            return new ImageSpan(drawable, verticalAlignment);
         }
      });
   }
   
   /**
    * @see ImageSpan#ImageSpan(Context, int, int)
    */
   public static com.tr.hsyn.telefonrehberi.util.spanner.ImageSpan image(final Context context, @DrawableRes final int drawableId, final int verticalAlignment){
      
      return new com.tr.hsyn.telefonrehberi.util.spanner.ImageSpan(new SpanBuilder(){
         
         @Override
         public Object build(){
            
            return new ImageSpan(context, drawableId, verticalAlignment);
         }
      });
   }
   
   /**
    * @see ImageSpan#ImageSpan(Context, int)
    */
   public static com.tr.hsyn.telefonrehberi.util.spanner.ImageSpan image(final Context context, @DrawableRes final int drawableId){
      
      return new com.tr.hsyn.telefonrehberi.util.spanner.ImageSpan(new SpanBuilder(){
         
         @Override
         public Object build(){
            
            return new ImageSpan(context, drawableId);
         }
      });
   }
   
   /**
    * @see QuoteSpan#QuoteSpan()
    */
   public static Span quote(){
      
      return new Span(new QuoteSpanBuilder(null));
   }
   
   /**
    * @see QuoteSpan#QuoteSpan(int)
    */
   public static Span quote(@ColorInt final int color){
      
      return new Span(new QuoteSpanBuilder(color));
   }
   
   /**
    * @see ImageSpan#ImageSpan(Context, Bitmap)
    */
   public static Span image(@NonNull Context context, Bitmap bitmap){
      
      return image(context, bitmap, ImageSpan.ALIGN_BOTTOM);
   }
   
   /**
    * @see ImageSpan#ImageSpan(Context, Bitmap, int)
    */
   public static Span image(@NonNull final Context context, final Bitmap bitmap, final int verticalAlignment){
      
      return new Span(new SpanBuilder(){
         
         @Override
         public Object build(){
            
            return new ImageSpan(context, bitmap, verticalAlignment);
         }
      });
   }
   
   public static Span custom(@NonNull SpanBuilder builder){
      
      return new Span(builder);
   }
   
   /**
    * Clicks won't work with default {@link TextView#getMovementMethod()}.
    * You must setValue {@link android.text.method.LinkMovementMethod}.<p>
    * Example:<p>
    * <pre><code>
    * textView.setMovementMethod(new LinkMovementMethod());
    * </code></pre>
    *
    * @see ClickableSpan
    */
   public static Span click(@NonNull final View.OnClickListener onClickListener){
      
      return new Span(new ClickSpanBuilder(onClickListener));
   }
   
   /**
    * Clicks won't work with default {@link TextView#getMovementMethod()}.
    *
    * @see #click(View.OnClickListener)
    * @see URLSpan#URLSpan(String)
    */
   public static Span url(@NonNull final String url){
      
      return new Span(new SpanBuilder(){
         
         @Override
         public Object build(){
            
            return new URLSpan(url);
         }
      });
   }
   
   /**
    * @see android.text.style.AlignmentSpan.Standard#Standard(Layout.Alignment)
    */
   public static Span center(){
      
      return new Span(new AlignmentSpanBuilder(Layout.Alignment.ALIGN_CENTER));
   }
   
   /**
    * @see android.text.style.AlignmentSpan.Standard#Standard(Layout.Alignment)
    */
   public static Span alignmentOpposite(){
      
      return new Span(new AlignmentSpanBuilder(Layout.Alignment.ALIGN_OPPOSITE));
   }
   
   /**
    * @see android.text.style.AlignmentSpan.Standard#Standard(Layout.Alignment)
    */
   public static Span alignmentNormal(){
      
      return new Span(new AlignmentSpanBuilder(Layout.Alignment.ALIGN_NORMAL));
   }
   
   /**
    * @see BulletSpan#BulletSpan(int, int)
    */
   public static Span bullet(final int gapWidth, @ColorInt final int color){
      
      return new Span(new BulletSpanBuilder(gapWidth, color));
   }
   
   /**
    * @see BulletSpan#BulletSpan(int)
    */
   public static Span bullet(final int gapWidth){
      
      return new Span(new BulletSpanBuilder(gapWidth, null));
   }
   
   /**
    * @see BulletSpan#BulletSpan()
    */
   public static Span bullet(){
      
      return new Span(new BulletSpanBuilder(null, null));
   }
   
   /**
    * @see android.text.style.DrawableMarginSpan#DrawableMarginSpan(Drawable)
    */
   public static Span imageMargin(@NonNull final Drawable drawable){
      
      return new Span(new ImageSpanBuilder(drawable, null, null));
   }
   
   /**
    * @see android.text.style.DrawableMarginSpan#DrawableMarginSpan(Drawable, int)
    */
   public static Span imageMargin(@NonNull final Drawable drawable, final int pad){
      
      return new Span(new ImageSpanBuilder(drawable, null, pad));
   }
   
   /**
    * @see EasyEditSpan
    * @see EasyEditSpan#EasyEditSpan()
    */
   public static Span edit(){
      
      return new Span(new SpanBuilder(){
         
         @Override
         public Object build(){
            
            return new EasyEditSpan();
         }
      });
   }
   
   /**
    * @see EasyEditSpan
    * @see EasyEditSpan#EasyEditSpan(PendingIntent)
    */
   @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
   public static Span edit(@NonNull final PendingIntent pendingIntent){
      
      return new Span(new SpanBuilder(){
         
         @Override
         public Object build(){
            
            return new EasyEditSpan(pendingIntent);
         }
      });
   }
   
   /**
    * @see android.text.style.IconMarginSpan#IconMarginSpan(Bitmap)
    */
   public static Span imageMargin(@NonNull final Bitmap image){
      
      return new Span(new ImageSpanBuilder(null, image, null));
   }
   
   /**
    * @see android.text.style.IconMarginSpan#IconMarginSpan(Bitmap, int)
    */
   public static Span imageMargin(@NonNull final Bitmap image, final int pad){
      
      return new Span(new ImageSpanBuilder(null, image, pad));
   }
   
   /**
    * @see android.text.style.LeadingMarginSpan
    * @see android.text.style.LeadingMarginSpan.Standard#Standard(int, int)
    */
   public static Span leadingMargin(final int first, final int rest){
      
      return new Span(new LeadingMarginSpanBuilder(first, rest));
   }
   
   /**
    * @see android.text.style.LeadingMarginSpan
    * @see android.text.style.LeadingMarginSpan.Standard#Standard(int)
    */
   public static Span leadingMargin(final int every){
      
      return new Span(new LeadingMarginSpanBuilder(every, null));
   }
   
   /**
    * @see BlurMaskFilter#BlurMaskFilter(float, BlurMaskFilter.Blur)
    */
   public static Span blur(final float radius, @NonNull final BlurMaskFilter.Blur style){
      
      return new Span(new SpanBuilder(){
         
         @Override
         public Object build(){
            
            return new MaskFilterSpan(new BlurMaskFilter(radius, style));
         }
      });
   }
   
   /**
    * @see EmbossMaskFilter#EmbossMaskFilter(float[], float, float, float)
    */
   public static Span emboss(@NonNull final float[] direction, final float ambient, final float specular, final float blurRadius){
      
      return new Span(new SpanBuilder(){
         
         @Override
         public Object build(){
            
            return new MaskFilterSpan(new EmbossMaskFilter(direction, ambient, specular, blurRadius));
         }
      });
   }
   
   /**
    * @see TabStopSpan
    */
   public static Span tabStop(final int where){
      
      return new Span(new SpanBuilder(){
         
         @Override
         public Object build(){
            
            return new TabStopSpan.Standard(where);
         }
      });
   }
   
   /**
    * @see TextAppearanceSpan#TextAppearanceSpan(Context, int)
    */
   public static Span appearance(@NonNull final Context context, final int appearance){
      
      return new Span(new SpanBuilder(){
         
         @Override
         public Object build(){
            
            return new TextAppearanceSpan(context, appearance);
         }
      });
   }
   
   /**
    * @see TextAppearanceSpan#TextAppearanceSpan(Context, int, int)
    */
   public static Span appearance(@NonNull final Context context, final int appearance, final int colorList){
      
      return new Span(new SpanBuilder(){
         
         @Override
         public Object build(){
            
            return new TextAppearanceSpan(context, appearance, colorList);
         }
      });
   }
   
   /**
    * @see TextAppearanceSpan#TextAppearanceSpan(String, int, int, ColorStateList, ColorStateList)
    */
   public static Span appearance(@Nullable final String family, final int style, final int size, @Nullable final ColorStateList color, @Nullable final ColorStateList linkColor){
      
      return new Span(new SpanBuilder(){
         
         @Override
         public Object build(){
            
            return new TextAppearanceSpan(family, style, size, color, linkColor);
         }
      });
   }
   
   /**
    * @see LocaleSpan#LocaleSpan(Locale)
    */
   @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
   public static Span locale(@NonNull final Locale locale){
      
      return new Span(new SpanBuilder(){
         
         @Override
         public Object build(){
            
            return new LocaleSpan(locale);
         }
      });
   }
   
   /**
    * @see LocaleSpan#LocaleSpan(LocaleList)
    */
   @RequiresApi(api = Build.VERSION_CODES.N)
   public static Span locale(@NonNull final LocaleList localeList){
      
      return new Span(new SpanBuilder(){
         
         @Override
         public Object build(){
            
            return new LocaleSpan(localeList);
         }
      });
   }
   
   /**
    * @see SuggestionSpan
    */
   public static Span suggestion(@NonNull final Context context, @NonNull final String[] suggestions, final int flags){
      
      return new Span(new SpanBuilder(){
         
         @Override
         public Object build(){
            
            return new SuggestionSpan(context, suggestions, flags);
         }
      });
   }
   
   /**
    * @see SuggestionSpan
    */
   public static Span suggestion(@NonNull final Locale locale, @NonNull final String[] suggestions, final int flags){
      
      return new Span(new SpanBuilder(){
         
         @Override
         public Object build(){
            
            return new SuggestionSpan(locale, suggestions, flags);
         }
      });
   }
   
   /**
    * @see SuggestionSpan
    */
   public static Span suggestion(@NonNull final Context context, @NonNull final Locale locale, @NonNull final String[] suggestions, final int flags, @NonNull final Class<?> notificationTargetClass){
      
      return new Span(new SpanBuilder(){
         
         @Override
         public Object build(){
            
            return new SuggestionSpan(context, locale, suggestions, flags, notificationTargetClass);
         }
      });
   }
}
