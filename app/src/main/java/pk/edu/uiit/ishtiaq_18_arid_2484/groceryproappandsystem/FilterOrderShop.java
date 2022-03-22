package pk.edu.uiit.ishtiaq_18_arid_2484.groceryproappandsystem;

import android.widget.Filter;

import java.util.ArrayList;

import pk.edu.uiit.ishtiaq_18_arid_2484.groceryproappandsystem.adapters.AdapterOrderShop;
import pk.edu.uiit.ishtiaq_18_arid_2484.groceryproappandsystem.models.ModelOrderShop;

public class FilterOrderShop extends Filter {

    AdapterOrderShop adapter;
    ArrayList<ModelOrderShop> filterList;

    public FilterOrderShop(AdapterOrderShop adapter, ArrayList<ModelOrderShop> filterList) {
        this.adapter = adapter;
        this.filterList = filterList;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();
        // Validate Data For Search Query
        if (constraint != null && constraint.length() > 0) {
            // Search Filed Not Empty, Searching Something, Perform Search
            // Change To Upper Case, To Make Insensitive
            constraint = constraint.toString().toUpperCase();
            // Store Our Filtered List
            ArrayList<ModelOrderShop> filteredModels = new ArrayList<>();
            for (int i=0; i<filterList.size(); i++){
                // Check, Search By Title And Category
                if (filterList.get(i).getOrderStatus().toUpperCase().contains(constraint)){
                    // Add Filtered Data To List
                    filteredModels.add(filterList.get(i));
                }
            }
            results.count = filteredModels.size();
            results.values = filteredModels;
        }
        else{
            // Search Filed Empty, Not Searching, Return Original/All/Complete List
            results.count = filterList.size();
            results.values = filterList;
        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        adapter.orderShopArrayList = (ArrayList<ModelOrderShop>) results.values;
        // Refresh Adapter
        adapter.notifyDataSetChanged();
    }
}
