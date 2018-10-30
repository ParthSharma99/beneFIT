package tech.iosd.benefit.DashboardFragments;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import tech.iosd.benefit.R;

public class Nutrition extends Fragment implements View.OnClickListener
{
    public boolean isMyNutritionLocked = false;

    Context ctx;
    FragmentManager fm;
    TextView myNutritionTxt;
    ImageView myNutritionLock;
    ImageView myNutritionProceed;
    CardView myNutritionCard;
    CardView nutritionInsightCard;
    ImageView proteinFacts,FoodSupplements,rejuvDetox,liquidDrinks,eatingMistakes,weightLoss,vitaminNeed,nutritionMyths,goodWeight,prePostWorkout;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.dashboard_nutrition, container, false);
        ctx = rootView.getContext();
        fm = getFragmentManager();

        myNutritionTxt = rootView.findViewById(R.id.dashboard_nutrition_my_txt);
        myNutritionLock = rootView.findViewById(R.id.dashboard_nutrition_my_lock);
        myNutritionProceed = rootView.findViewById(R.id.dashboard_nutrition_my_proceed);
        myNutritionCard = rootView.findViewById(R.id.dashboard_nutrition_my_nutrition);
        nutritionInsightCard = rootView.findViewById(R.id.dashboard_nutrition_insight);
        proteinFacts = rootView.findViewById(R.id.dashboard_nutrition_protein_facts);
        FoodSupplements = rootView.findViewById(R.id.dashboard_nutrition_food_supplements);
        rejuvDetox=rootView.findViewById(R.id.dashboard_nutrition_rejuvenate_with_detox);
        liquidDrinks=rootView.findViewById(R.id.dashboard_nutrition_liquid_nutritional_drinks);
        eatingMistakes=rootView.findViewById(R.id.dashboard_nutrition_commom_eating_mistakes);
        weightLoss=rootView.findViewById(R.id.dashboard_nutrition_weight_loss_formula);
        vitaminNeed=rootView.findViewById(R.id.dashboard_nutrition_need_of_vitamins);
        nutritionMyths=rootView.findViewById(R.id.dashboard_nutrition_myths_facts);
        goodWeight=rootView.findViewById(R.id.dashboard_nutrition_how_to_gain_good_weight);
        prePostWorkout=rootView.findViewById(R.id.dashboard_nutrition_pre_post_workout);
        setMyNutritionLockCondition(isMyNutritionLocked);

        myNutritionCard.setOnClickListener(this);
        nutritionInsightCard.setOnClickListener(this);
        proteinFacts.setOnClickListener(this);
        FoodSupplements.setOnClickListener(this);
        rejuvDetox.setOnClickListener(this);
        liquidDrinks.setOnClickListener(this);
        eatingMistakes.setOnClickListener(this);
        weightLoss.setOnClickListener(this);
        vitaminNeed.setOnClickListener(this);
        nutritionMyths.setOnClickListener(this);
        goodWeight.setOnClickListener(this);
        prePostWorkout.setOnClickListener(this);
        return rootView;
    }

    void setMyNutritionLockCondition(boolean isLocked)
    {
        if(!isLocked)
        {
            myNutritionLock.setVisibility(View.INVISIBLE);
            myNutritionProceed.setVisibility(View.VISIBLE);
            myNutritionTxt.setTextColor(Color.BLACK);
        }
        else
        {
            myNutritionLock.setVisibility(View.VISIBLE);
            myNutritionProceed.setVisibility(View.INVISIBLE);
            myNutritionTxt.setTextColor(Color.parseColor("#9d9d9d"));
        }
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.dashboard_nutrition_my_nutrition:
            {
                if(isMyNutritionLocked)
                    fm.beginTransaction().replace(R.id.dashboard_content, new MyNutritionLocked()).addToBackStack(null).commit();
                else
                    fm.beginTransaction().replace(R.id.dashboard_content, new MyNutrition()).addToBackStack(null).commit();
                break;
            }
            case R.id.dashboard_nutrition_protein_facts:
                fm.beginTransaction().replace(R.id.dashboard_content, new ProteinFacts()).addToBackStack(null).commit();
                break;
            case R.id.dashboard_nutrition_food_supplements:
                fm.beginTransaction().replace(R.id.dashboard_content, new FoodSupplements()).addToBackStack(null).commit();
                break;
            case R.id.dashboard_nutrition_rejuvenate_with_detox:
                fm.beginTransaction().replace(R.id.dashboard_content, new RejuvenateDetox()).addToBackStack(null).commit();
                break;
            case R.id.dashboard_nutrition_liquid_nutritional_drinks:
                fm.beginTransaction().replace(R.id.dashboard_content, new LiquidDrink()).addToBackStack(null).commit();
                break;
            case R.id.dashboard_nutrition_commom_eating_mistakes:
                fm.beginTransaction().replace(R.id.dashboard_content, new EatingMistakes()).addToBackStack(null).commit();
                break;
            case R.id.dashboard_nutrition_weight_loss_formula:
                fm.beginTransaction().replace(R.id.dashboard_content, new WeightLossFormula()).addToBackStack(null).commit();
                break;
            case R.id.dashboard_nutrition_need_of_vitamins:
                fm.beginTransaction().replace(R.id.dashboard_content, new VitaminNeed()).addToBackStack(null).commit();
                break;
            case R.id.dashboard_nutrition_myths_facts:
                fm.beginTransaction().replace(R.id.dashboard_content, new NutritionMyths()).addToBackStack(null).commit();
                break;
            case R.id.dashboard_nutrition_how_to_gain_good_weight:
                fm.beginTransaction().replace(R.id.dashboard_content, new GoodWeight()).addToBackStack(null).commit();
                break;
            case R.id.dashboard_nutrition_pre_post_workout:
                fm.beginTransaction().replace(R.id.dashboard_content, new PrePostWorkout()).addToBackStack(null).commit();
                break;
        }
    }
}
