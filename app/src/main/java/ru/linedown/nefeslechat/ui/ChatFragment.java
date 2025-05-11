package ru.linedown.nefeslechat.ui;

import static android.content.Context.MODE_PRIVATE;
import static android.view.View.TEXT_ALIGNMENT_CENTER;

import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.gson.Gson;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import ru.linedown.nefeslechat.R;
import ru.linedown.nefeslechat.classes.ConfirmExitDialogFragment;
import ru.linedown.nefeslechat.classes.ConfirnDeleteMessageDialogFragment;
import ru.linedown.nefeslechat.classes.MyStompSessionHandler;
import ru.linedown.nefeslechat.classes.WebSocketConnection;
import ru.linedown.nefeslechat.classes.MessageLayout;
import ru.linedown.nefeslechat.classes.OkHttpUtil;
import ru.linedown.nefeslechat.databinding.FragmentChatBinding;
import ru.linedown.nefeslechat.entity.MessageAllInfoDTO;
import ru.linedown.nefeslechat.entity.MessageLayoutAttributes;
import ru.linedown.nefeslechat.entity.MessageSendDTO;
import ru.linedown.nefeslechat.enums.MessageTypeEnum;
import ru.linedown.nefeslechat.entity.WebSocketDTO;
import ru.linedown.nefeslechat.interfaces.MyCallback;

public class ChatFragment extends Fragment {
    final String JWT_TOKEN = "jwt_token";
    private FragmentChatBinding binding;
    private int userId;
    private int chatId;
    LinearLayout chatFormLayout;
    String chatType;
    Disposable disposableInner;
    Disposable loadMessageDisposable;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentChatBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        Bundle arguments = getArguments();
        String toolbarTitle = arguments.getString("TitleToolBar");
        toolbar.setTitle(toolbarTitle);
        chatType = arguments.getString("ChatType");

        chatId = Integer.parseInt(arguments.getString("ChatId"));

        if(chatType.equals("Single")){
            userId = Integer.parseInt(arguments.getString("UserId"));
            Log.d("Id собеседника: ", "" + userId);
        }

        EditText inputField = binding.messageText;
        ImageView sendTextButton = binding.sendTextButton;
        ImageView sendFileButton = binding.sendFileButton;
        chatFormLayout = binding.chatFormLayout;

        WebSocketConnection.subscribeOnSendMessageEvent(message -> {
            MessageAllInfoDTO messageInChatDTO = new Gson().fromJson(message, MessageAllInfoDTO.class);
            addMessageInChat(messageInChatDTO);
        });

        Observable<List<MessageAllInfoDTO>> observable = Observable.fromCallable(() -> OkHttpUtil.getMessagesInChat(chatId));
        MyCallback<List<MessageAllInfoDTO>> mcOnMessages = new MyCallback<>() {
            @Override
            public void onSuccess(List<MessageAllInfoDTO> result) {
                for(MessageAllInfoDTO message : result) addMessageInChat(message);
            }

            @Override
            public void onError(String errorMessage) {
                Log.d("ChatFragment", "Ошибка с получением сообщений в чате: " + errorMessage);
            }
        };

        loadMessageDisposable = observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(mcOnMessages::onSuccess, error -> mcOnMessages.onError(error.getMessage()));

        // Что происходит при нажатии на изображение файла? ВОПРОС ХОРОШИЙ
        sendFileButton.setOnClickListener(view -> {});

        sendTextButton.setOnClickListener(view -> {
            Observable<WebSocketDTO> observableInner = Observable.fromCallable(() -> {
                String text = inputField.getText().toString();
                if(text.isBlank()) return null;
                MessageSendDTO messageDTO = new MessageSendDTO(MessageTypeEnum.TEXT, text);
                return new WebSocketDTO("sendMessage", messageDTO);
            });

            disposableInner = observableInner.subscribeOn(Schedulers.io()).observeOn(Schedulers.io())
                    .subscribe(result -> {
                        if(result == null) return;
                        try {
                            if (WebSocketConnection.isConnected()){
                                Log.w("Ссылка", OkHttpUtil.getChatUrl() + chatId);
                                if(chatType.equals("Single")) WebSocketConnection.getSession().send(OkHttpUtil.getUserUrl() + userId, result);
                                else WebSocketConnection.getSession().send(OkHttpUtil.getChatUrl() + chatId, result);
                                inputField.setText("");
                            }
                            else Log.w("WebSocket", "Соединение WebSocket не активно. Сообщение не отправлено.");
                        } catch (Exception e) {
                            Log.e("Отправка сообщения", "Ошибка отправки: " + e.getMessage(), e);
                        }
                    }, error -> Log.e("Отправка: исключение", "Текст исключения: " + error.getMessage(), error));
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        Log.d("ChatFragment", "onDestroyView() вызван");

        if (disposableInner != null && !disposableInner.isDisposed()) {
            disposableInner.dispose();
            Log.d("ChatFragment", "Disposable (observableInner) отписан");
        }
        if(loadMessageDisposable != null && !loadMessageDisposable.isDisposed()){
            loadMessageDisposable.dispose();
            Log.d("ChatFragment", "Disposable (messageDisposable) отписан");
        }
        WebSocketConnection.unSubscribeOnSendMessageEvent();
        binding = null;
        Log.d("ChatFragment", "binding обнулен");
    }

    public void addMessageInChat(MessageAllInfoDTO messageInChatDTO){
        int typeSender;
        if(messageInChatDTO.getSenderName() == null){
            TextView joinView = new TextView(getContext());
            joinView.setTextColor(getResources().getColor(R.color.readChatColor));
            joinView.setId(messageInChatDTO.getId());
            joinView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            joinView.setPadding(0,
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getContext().getResources().getDisplayMetrics()),
                    0,
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getContext().getResources().getDisplayMetrics()));
            joinView.setTypeface(ResourcesCompat.getFont(getContext(), R.font.inter_medium));
            joinView.setText(messageInChatDTO.getMessage());
            joinView.setTextAlignment(TEXT_ALIGNMENT_CENTER);
            chatFormLayout.addView(joinView);
        }
        else {
            MessageLayoutAttributes mla = new MessageLayoutAttributes(
                messageInChatDTO.getId(), messageInChatDTO.getCreatedAt(), messageInChatDTO.getMessage(),
                messageInChatDTO.getFilename(), chatType, messageInChatDTO.getSenderName());

            if(messageInChatDTO.getSenderId() == OkHttpUtil.getMyId()) typeSender = MessageLayout.ME;
            else typeSender = MessageLayout.COMPANION;
            MessageLayout messageLayout = new MessageLayout(getActivity(), typeSender, mla);
            messageLayout.setOnClickListener(view -> {
                ConfirnDeleteMessageDialogFragment confirmExitDialogFragment = new ConfirnDeleteMessageDialogFragment(messageLayout, userId, chatId);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                confirmExitDialogFragment.show(transaction, "dialog");
            });
            chatFormLayout.addView(messageLayout);
        }
    }
}