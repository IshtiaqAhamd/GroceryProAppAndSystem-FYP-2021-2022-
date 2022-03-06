package pk.edu.uiit.ishtiaq_18_arid_2484.groceryproappandsystem;

import android.widget.Filter;

import java.util.ArrayList;

import pk.edu.uiit.ishtiaq_18_arid_2484.groceryproappandsystem.adapters.AdapterProductUser;
import pk.edu.uiit.ishtiaq_18_arid_2484.groceryproappandsystem.models.ModelProduct;

public class FilterProductUser extends Filter {

    AdapterProductUser adapter;
    ArrayList<ModelProduct> filterList;

    public FilterProductUser(AdapterProductUser adapter, ArrayList<ModelProduct> filterList) {
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
            ArrayList<ModelProduct> filteredModels = new ArrayList<>();
            for (int i=0; i<filterList.size(); i++){
                // Check, Search By Title And Category
                if (filterList.get(i).getProductTitle().toUpperCase().contains(constraint) ||
                        filterList.get(i).getProductDescription().toUpperCase().contains(constraint)){
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
        adapter.productsList = (ArrayList<ModelProduct>) results.values;
        // Refresh Adapter
        adapter.notifyDataSetChanged();
    }
}
