package tech.iosd.benefit.Network;

/**
 * Created by SAM33R on 25-05-2018.
 */

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

import tech.iosd.benefit.Model.BodyForChangePassword;
import tech.iosd.benefit.Model.BodyForMealLog;
import tech.iosd.benefit.Model.Measurements;
import tech.iosd.benefit.Model.PostFreeWorkoutActivity;
import tech.iosd.benefit.Model.PostTrackActivity;
import tech.iosd.benefit.Model.PostWaterIntake;
import tech.iosd.benefit.Model.Response;
import tech.iosd.benefit.Model.ResponseForChatMessage;
import tech.iosd.benefit.Model.ResponseForFoodSearch;
import tech.iosd.benefit.Model.ResponseForGetExcerciseVideoUrl;
import tech.iosd.benefit.Model.ResponseForGetMeal;
import tech.iosd.benefit.Model.ResponseForMeasurementsUpdate;
import tech.iosd.benefit.Model.ResponseForMesurementsHistory;
import tech.iosd.benefit.Model.ResponseForSuccess;
import tech.iosd.benefit.Model.ResponseForUpdate;
import tech.iosd.benefit.Model.ResponseForWorkoutForDate;
import tech.iosd.benefit.Model.ResponseNutritionPlanForDate;
import tech.iosd.benefit.Model.ResponseTrackingDetails;
import tech.iosd.benefit.Model.ResponseWaterIntake;
import tech.iosd.benefit.Model.ResponseWorkoutFree;
import tech.iosd.benefit.Model.User;
import tech.iosd.benefit.Model.UserDetails;
import tech.iosd.benefit.Model.UserFacebookLogin;
import tech.iosd.benefit.Model.UserForLogin;
import tech.iosd.benefit.Model.UserGoogleLogin;
import tech.iosd.benefit.Model.UserProfileUpdate;

public interface RetrofitInterface {

    @POST("auth/signup")
    Observable<Response> register(@Body User user);

    @POST("auth/login")
    Observable<Response> login(@Body UserForLogin userForLogin);

    @POST("auth/login/google")
    Observable<Response> loginGoogle(@Body UserGoogleLogin userGoogleLogin);

    @POST("auth/login/facebook")
    Observable<Response> loginFacebook(@Body UserFacebookLogin userGoogleLogin);

    @POST("profile/update")
    Observable<ResponseForUpdate> update(@Header("authorization") String token, @Body UserProfileUpdate userProfileUpdate);

    @POST("profile/measurements")
    Observable<ResponseForMeasurementsUpdate> updateMeasurements(@Header("authorization") String token, @Body Measurements measurements);

    @GET("profile")
    Observable<UserDetails> getProfile(@Header("Authorization") String token);

    @GET("profile/measurements/history")
    Observable<ResponseForMesurementsHistory> getMeasurementsHistory(@Header("Authorization") String token);

    @GET("mealLog/food/search/{name}")
    Observable<ResponseForFoodSearch> getFoodList(@Path("name") String name, @Header("Authorization") String token);

    @GET("workout/user/get")
    Observable<ResponseForWorkoutForDate> getWorkoutforDate(@Query("date") String date, @Header("Authorization") String token);

    @GET("chat/fetch")
    Observable<ResponseForChatMessage> getChatMessages(@Query("timestamp") Long timestamp, @Header("Authorization") String token);

    @GET("mealLog/details")
    Observable<ResponseForGetMeal> getFoodMeal(@Query("date") String date, @Query("type") String type, @Header("Authorization") String token);

    @POST("mealLog/details")
    Observable<ResponseForSuccess> sendFoodMeal(@Body BodyForMealLog bodyForMealLog, @Header("Authorization") String token);

    @GET("workout/exercise/{url}/url/")
    Observable<ResponseForGetExcerciseVideoUrl> getExerciseVideoUrl(@Path("url") String url, @Header("Authorization") String token, @Query("type") String type);
    /*@GET("users/{email}")
    Observable<User> getProfile(@Path("email") String email);*/

    @POST("auth/changePassword")
    Observable<Response> changePassword(@Header("authorization") String token, @Body BodyForChangePassword userProfileUpdate);

    @PUT("users/{email}")
    Observable<Response> changePassword(@Path("email") String email, @Body User user);

    @POST("users/{email}/password")
    Observable<Response> resetPasswordInit(@Path("email") String email);

    @POST("users/{email}/password")
    Observable<Response> resetPasswordFinish(@Path("email") String email, @Body User user);

    @GET("nutrition/plan/get")
    Observable<ResponseNutritionPlanForDate> getNutritionPlanForDate(@Query("date") String date, @Header("Authorization") String token);

    @GET("workout/free")
    Observable<ResponseWorkoutFree> getWorkoutFree(@Header("Authorization") String token);

    @GET("tracking/details")
    Observable<ResponseTrackingDetails> getTrackActivityDetails(@Header("Authorization") String token, @Query("date") String date);

    @POST("tracking/details")
    Observable<ResponseForSuccess> sendActivityDetails(@Body PostTrackActivity postTrackActivity, @Header("Authorization") String token);

    @POST("workout/user/complete/free")
    Observable<ResponseForSuccess> sendFreeWorkoutActivity(@Body PostFreeWorkoutActivity postTrackActivity, @Header("Authorization") String token);

    @POST("workout/user/complete")
    Observable<ResponseForSuccess> sendWorkoutActivity(@Body PostFreeWorkoutActivity postTrackActivity, @Header("Authorization") String token);

    @POST("log/water/details")
    Observable<ResponseForSuccess> sendWaterIntake(@Body PostWaterIntake postWaterIntake, @Header("Authorization") String token);

    @GET("log/water/history")
    Observable<ResponseWaterIntake> getWaterIntakeHistory( @Header("Authorization") String token);
}