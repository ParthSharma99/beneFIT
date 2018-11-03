package tech.iosd.benefit.OnBoardingFragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import tech.iosd.benefit.Adapters.Goals;
import tech.iosd.benefit.R;

public class ChooseAGoal extends Fragment implements ViewPager.OnPageChangeListener, View.OnClickListener
{
    FragmentManager fm;
    ArrayList<ImageView> pagerIndicator;
    public static int goalID =0;
    /*
    0: Build Muscle
    1: Fat loss
    2:  Stay fit
     */

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.onboarding_choose_a_goal, container, false);
        fm = getFragmentManager();

        ViewPager viewPager = rootView.findViewById(R.id.get_started_goals_pager);
        ArrayList<Integer> goalImages = new ArrayList<>();
        goalImages.add(R.drawable.build_muscle);
        goalImages.add(R.drawable.burn_fat);
        goalImages.add(R.drawable.stay_healthy);
        pagerIndicator = new ArrayList<>();
        pagerIndicator.add((ImageView)rootView.findViewById(R.id.get_started_pager_indicator1));
        pagerIndicator.add((ImageView)rootView.findViewById(R.id.get_started_pager_indicator2));
        pagerIndicator.add((ImageView)rootView.findViewById(R.id.get_started_pager_indicator3));
        onPageSelected(0);

        viewPager.setAdapter(new Goals(getChildFragmentManager(), goalImages));
        viewPager.addOnPageChangeListener(this);

        rootView.findViewById(R.id.get_started_continue).setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onPageSelected(int position)
    {
        for(int i=0; i<pagerIndicator.size(); i++)
            pagerIndicator.get(i).setColorFilter(getResources().getColor(R.color.pageIndicatorNotSelected));

        pagerIndicator.get(position).setColorFilter(getResources().getColor(R.color.pageIndicatorSelected));
        goalID = position;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageScrollStateChanged(int state) { }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.get_started_continue:
            {

                SetupProfile setupProfile = new SetupProfile();
                Bundle bundle = new Bundle();
                bundle.putInt("goalID",goalID);
                setupProfile.setArguments(bundle);
                fm.beginTransaction().replace(R.id.onboarding_content, setupProfile)
                        .addToBackStack(null)
                        .commit();
                break;
            }
        }
    }
}
