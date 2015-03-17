package com.carl.netspeednotification.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carl.netspeednotification.R;
import com.carl.netspeednotification.Umeng;

abstract public class BaseFragment extends Fragment {
    private View mRootView;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(getLayoutRes(), container, false);
        initViews(mRootView);
        initDatas();
        return mRootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Umeng.onFragmentResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        Umeng.onFragmentPause(this);
    }

    protected void openFragment(Fragment fromFragment, Class<?> fregmentClass, Bundle arguments) {
        Fragment fragment = Fragment.instantiate(getActivity(), fregmentClass.getName(), arguments);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        // transaction.setCustomAnimations(R.anim.right_in, R.anim.left_out, R.anim.left_in, R.anim.right_out);
        try {
            transaction.hide(fromFragment);
            transaction.add(R.id.content, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected View findViewById(int id){
        if (mRootView == null){
            return null;
        }
        return mRootView.findViewById(id);
    }

    /**
     * 接收返回键按下事件
     * @Title: onBackKeyDown
     * @return boolean  false:back键事件未处理，向下传递。  true：消费掉该事件。
     * @date 2014-3-10 上午11:15:33
     */
    protected boolean onBackKeyDown(){
        return false;
    }
    
    abstract public int getLayoutRes();
    
    abstract public void initViews(View root);
    
    abstract public void initDatas();
}
