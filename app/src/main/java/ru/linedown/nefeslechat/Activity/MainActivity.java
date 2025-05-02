package ru.linedown.nefeslechat.Activity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

import androidx.core.view.GravityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import ru.linedown.nefeslechat.R;
import ru.linedown.nefeslechat.classes.ConfirmExitDialogFragment;
import ru.linedown.nefeslechat.classes.OkHttpUtil;
import ru.linedown.nefeslechat.classes.UserDetailsDTO;
import ru.linedown.nefeslechat.databinding.ActivityMainBinding;
import ru.linedown.nefeslechat.interfaces.MyCallbackForUser;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private UserDetailsDTO currentUser;
    private Disposable disposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        View infoBarView = navigationView.getHeaderView(0);
        TextView infoInBar = infoBarView.findViewById(R.id.InfoLabel);
        TextView loginInBar = infoBarView.findViewById(R.id.mailLabel);
        TextView userInBar = infoBarView.findViewById(R.id.userLabel);
        CircleImageView civ = infoBarView.findViewById(R.id.avatar);
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);

        Observable<UserDetailsDTO> observable = Observable.fromCallable(() -> {
            try{
                return OkHttpUtil.getCurrentUser();
            } catch (IOException e) {
                Log.d("IOException", "NavHeaderMain. Текст сообщения: " + e.getMessage());
            }
            return null;
        });

        MyCallbackForUser mcfu = new MyCallbackForUser() {
            @Override
            public void onSuccess(UserDetailsDTO result) {
                String role = result.getRole();
                String fio = result.getLastName() + " " + result.getFirstName() + " " + result.getPatronymic();
                userInBar.setTypeface(null, Typeface.BOLD);
                userInBar.setText(fio);
                if(role.equals("Преподаватель")){
                    String info = result.getAcademicTitle() + " • " + result.getAcademicDegree();
                    infoInBar.setText(info);
                } else {
                    String info = role + " • " + result.getGroupName();
                    infoInBar.setText(info);
                }
                loginInBar.setText(result.getEmail());
            }

            @Override
            public void onError(String errorMessage) {
                Log.d("ObserveError", "Текст ошибки: " + errorMessage);
            }
        };

        disposable = observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        mcfu::onSuccess, error -> mcfu.onError(error.getMessage())
                );

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_chats, R.id.nav_settings,
                R.id.nav_create_chat, R.id.nav_search, R.id.nav_about,
                R.id.nav_notes, R.id.nav_raspisanie, R.id.nav_logout)
                .setOpenableLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_logout) {
                ConfirmExitDialogFragment confirmExitDialogFragment = new ConfirmExitDialogFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                confirmExitDialogFragment.show(transaction, "dialog");
                return true;
            } else {
                boolean handled = NavigationUI.onNavDestinationSelected(item, navController);
            if (handled) {
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            } else return false;
        }
        });
    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }
}
