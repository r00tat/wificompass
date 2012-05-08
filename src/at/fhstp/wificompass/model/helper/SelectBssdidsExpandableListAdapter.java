package at.fhstp.wificompass.model.helper;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import at.fhstp.wificompass.R;
import at.fhstp.wificompass.model.Bssid;

public class SelectBssdidsExpandableListAdapter extends
		BaseExpandableListAdapter {

	private Context context;

	private ArrayList<String> groups;

	private ArrayList<ArrayList<Bssid>> children;

	public SelectBssdidsExpandableListAdapter() {
	}
	
	public SelectBssdidsExpandableListAdapter(Context context,
			ArrayList<String> groups, ArrayList<ArrayList<Bssid>> children) {
		initialize(context, groups, children);
	}

	public void initialize(Context context,
			ArrayList<String> groups, ArrayList<ArrayList<Bssid>> children) {
		this.context = context;
		this.groups = groups;
		this.children = children;
	}
	
	/**
	 * Adds a BSSID to the list. The SSID string is taken as the group that
	 * categorizes the BSSIDs.
	 * 
	 * @param bssid
	 *            The BSSID to be added to the List
	 */
	public void addItem(Bssid bssid) {
		if (!groups.contains(bssid.getSsid())) {
			groups.add(bssid.getSsid());
		}

		int index = groups.indexOf(bssid.getSsid());

		if (children.size() < index + 1) {
			children.add(new ArrayList<Bssid>());
		}
		children.get(index).add(bssid);
	}

	public void addItems(ArrayList<Bssid> items) {
		for (Bssid item : items) {
			this.addItem(item);
		}
	}
	
	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return children.get(groupPosition).get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {

		final Bssid bssid = (Bssid) getChild(groupPosition, childPosition);
		if (convertView == null) {
			LayoutInflater infalInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infalInflater.inflate(
					R.layout.project_site_dialog_select_bssid_child_layout,
					null);
		}
		final TextView tv = (TextView) convertView
				.findViewById(R.id.project_site_dialog_select_bssids_child_text);
		final ImageView iv = (ImageView) convertView
				.findViewById(R.id.project_site_dialog_select_bssids_checkbox_image);
		tv.setText(bssid.getBssid());

		if (bssid.isSelected() == true)
			iv.setImageResource(android.R.drawable.checkbox_on_background);
		else
			iv.setImageResource(android.R.drawable.checkbox_off_background);

		tv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (bssid.isSelected() == true) {
					bssid.setSelected(false);
					iv.setImageResource(android.R.drawable.checkbox_off_background);
				} else {
					bssid.setSelected(true);
					iv.setImageResource(android.R.drawable.checkbox_on_background);
				}
			}
		});

		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return children.get(groupPosition).size();
	}

	public ArrayList<Bssid> getChildrenByGroup(int groupPosition) {
		String group = (String) getGroup(groupPosition);
		int index = groups.indexOf(group);
		return children.get(index);
	}
	
	@Override
	public Object getGroup(int groupPosition) {
		return groups.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return groups.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	/**
	 * Return a group view.
	 */
	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		String group = (String) getGroup(groupPosition);
		if (convertView == null) {
			LayoutInflater infalInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infalInflater.inflate(
					R.layout.project_site_dialog_select_bssids_parent_layout,
					null);
		}
		TextView tv = (TextView) convertView
				.findViewById(R.id.project_site_dialog_select_bssids_parent_text);
		tv.setText(group);
		
		final ExpandableListView parentList = (ExpandableListView)parent;
		final int groupPositionList = groupPosition;
		final boolean isExpandedList = isExpanded;
		tv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!isExpandedList) {
					parentList.expandGroup(groupPositionList);
				} else {
					parentList.collapseGroup(groupPositionList);
				}
			}
			
		});
		
		TextView tvAll = (TextView) convertView
				.findViewById(R.id.project_site_dialog_select_bssids_parent_all_text);
		CheckBox cbAll = (CheckBox) convertView
				.findViewById(R.id.project_site_dialog_select_bssids_parent_all_checkbox);
		
		if (areAllChildrenSelected(groupPosition)) {
			cbAll.setChecked(true);
		} else {
			cbAll.setChecked(false);
		}
			
		
		final boolean allSelected = cbAll.isChecked();
		
		OnClickListener selectAllListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				selectAllChildrenOfGroup(groupPositionList, allSelected, parentList);
			}
		};
		
		cbAll.setOnClickListener(selectAllListener);
		tvAll.setOnClickListener(selectAllListener);
		
		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
	
	public void selectAllChildrenOfGroup(int groupPosition, boolean selected, ExpandableListView parentList) {
		ArrayList<Bssid> children = getChildrenByGroup(groupPosition);
		
		for (Bssid b : children) {
			b.setSelected(!selected);
		}
		
		this.notifyDataSetChanged();
	}

	public void selectAllChildren(boolean state) {
		for (String g : groups) {
			ArrayList<Bssid> children = getChildrenByGroup(groups.indexOf(g));
			
			for (Bssid b : children) {
				b.setSelected(state);
			}
		}

		this.notifyDataSetChanged();
	}
	
	public boolean areAllChildrenSelected(int groupPosition) {
		boolean allSelected = true;
		
		ArrayList<Bssid> children = getChildrenByGroup(groupPosition);
		
		for (Bssid b : children) {
			if (!b.isSelected())
				allSelected = false;
		}
		
		return allSelected;
	}
	
	public ArrayList<Bssid> getSelectedBssids() {
		ArrayList<Bssid> selectedBssids = new ArrayList<Bssid>();
		
		for (String g : groups) {
			ArrayList<Bssid> children = getChildrenByGroup(groups.indexOf(g));
			
			for (Bssid b : children) {
				if (b.isSelected())
					selectedBssids.add(b);
			}
		}
		
		return selectedBssids;
	}
}
