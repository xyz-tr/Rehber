package com.tr.hsyn.telefonrehberi.util.ui.searchview;


import com.tr.hsyn.telefonrehberi.util.ui.searchview.model.Filter;

import java.util.List;


public interface OnFilterViewListener{
   
   void onFilterAdded(Filter filter);
   
   void onFilterRemoved(Filter filter);
   
   void onFilterChanged(List<Filter> list);
}
