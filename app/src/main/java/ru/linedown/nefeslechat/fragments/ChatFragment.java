package ru.linedown.nefeslechat.fragments;

import static android.view.View.TEXT_ALIGNMENT_CENTER;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.gson.Gson;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import ru.linedown.nefeslechat.R;
import ru.linedown.nefeslechat.layuots.ChoseActionDialogFragment;
import ru.linedown.nefeslechat.layuots.ConfirmDeleteMessageDialogFragment;
import ru.linedown.nefeslechat.layuots.EditMessageDialogFragment;
import ru.linedown.nefeslechat.utils.WebSocketConnection;
import ru.linedown.nefeslechat.layuots.MessageLayout;
import ru.linedown.nefeslechat.utils.OkHttpUtil;
import ru.linedown.nefeslechat.databinding.FragmentChatBinding;
import ru.linedown.nefeslechat.entity.EditMessagePayload;
import ru.linedown.nefeslechat.entity.MessageAllInfoDTO;
import ru.linedown.nefeslechat.entity.MessageLayoutAttributes;
import ru.linedown.nefeslechat.entity.MessageSendDTO;
import ru.linedown.nefeslechat.enums.MessageTypeEnum;
import ru.linedown.nefeslechat.entity.WebSocketDTO;
import ru.linedown.nefeslechat.interfaces.ChoseActionListener;
import ru.linedown.nefeslechat.interfaces.DeleteMessageActionListener;
import ru.linedown.nefeslechat.interfaces.EditMessageActionListener;
import ru.linedown.nefeslechat.interfaces.MyCallback;

public class ChatFragment extends Fragment implements ChoseActionListener, EditMessageActionListener, DeleteMessageActionListener {
    final int MS = 69;
    private FragmentChatBinding binding;
    private int userId;
    private int chatId;
    LinearLayout chatFormLayout;
    String chatType;
    Disposable disposableInner;
    Disposable loadMessageDisposable;
    Disposable deleteDisposable;
    ScrollView scrollViewInChat;
    String messageLayoutChooseText;
    MessageLayout chooseLayout;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentChatBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        Bundle arguments = getArguments();
        String toolbarTitle = arguments.getString("TitleToolBar");
        toolbar.setTitle(toolbarTitle);
        chatType = arguments.getString("ChatType");
        scrollViewInChat = binding.scrollViewInChat;

        chatId = Integer.parseInt(arguments.getString("ChatId"));
        Log.d("Айди чата", "" + chatId);

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
                new Handler(Looper.getMainLooper()).postDelayed(() -> scrollViewInChat.fullScroll(View.FOCUS_DOWN), MS);
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
                                if(chatType.equals("Single")) WebSocketConnection.send(OkHttpUtil.getUserUrl() + userId, result);
                                else WebSocketConnection.send(OkHttpUtil.getChatUrl() + chatId, result);
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
        clearAllDialogFragment();

        if (disposableInner != null && !disposableInner.isDisposed()) {
            disposableInner.dispose();
            Log.d("ChatFragment", "Disposable (observableInner) отписан");
        }
        if(loadMessageDisposable != null && !loadMessageDisposable.isDisposed()){
            loadMessageDisposable.dispose();
            Log.d("ChatFragment", "Disposable (messageDisposable) отписан");
        }
        if(deleteDisposable != null && !deleteDisposable.isDisposed()){
            deleteDisposable.dispose();
            Log.d("ChatFragment", "Disposable (deleteMessageDisposable) отписан");
        }
        WebSocketConnection.unSubscribeOnSendMessageEvent();
        binding = null;
        Log.d("ChatFragment", "binding обнулен");
    }

    public void addMessageInChat(MessageAllInfoDTO messageInChatDTO){
        int typeSender;
        if(messageInChatDTO.getSenderName() == null){
            TextView infoView = new TextView(getContext());
            infoView.setTextColor(getResources().getColor(R.color.readChatColor));
            infoView.setId(messageInChatDTO.getId());
            infoView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            infoView.setPadding(0,
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getContext().getResources().getDisplayMetrics()),
                    0,
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getContext().getResources().getDisplayMetrics()));
            infoView.setTypeface(ResourcesCompat.getFont(getContext(), R.font.inter_medium));
            infoView.setText(messageInChatDTO.getMessage());
            infoView.setTextAlignment(TEXT_ALIGNMENT_CENTER);
            chatFormLayout.addView(infoView);
        }
        else {
            MessageLayoutAttributes mla = new MessageLayoutAttributes(
                messageInChatDTO.getId(), messageInChatDTO.getCreatedAt(), messageInChatDTO.getMessage(),
                messageInChatDTO.getFilename(), chatType, messageInChatDTO.getSenderName());

            if(messageInChatDTO.getSenderId() == OkHttpUtil.getMyId()) typeSender = MessageLayout.ME;
            else typeSender = MessageLayout.COMPANION;
            MessageLayout messageLayout = new MessageLayout(getActivity(), typeSender, mla);
            if (typeSender == MessageLayout.ME){
                messageLayout.setOnClickListener(view -> {
                    messageLayoutChooseText = messageInChatDTO.getMessage();
                    chooseLayout = messageLayout;
                    showChooseActionDialog();
                });
            }
            chatFormLayout.addView(messageLayout);
            new Handler(Looper.getMainLooper()).postDelayed(() -> scrollViewInChat.fullScroll(View.FOCUS_DOWN), MS);
        }
    }

    @Override
    public void onDeleteAction() {
        showDeleteActionDialog();
    }

    @Override
    public void onEditAction() {
        showEditActionDialog();
    }

    @Override
    public void onCancelAction() {
        Toast.makeText(getContext(), "Действие по выбору отменено", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onApplyDelete() {
        deleteAction();
        Toast.makeText(getContext(), "Действие по удалению выполнено", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCancelDelete() {
        Toast.makeText(getContext(), "Действие по удалению отменено", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onApplyEdit(String newText) {
        int messageId = chooseLayout.getId();
        Observable<String> observable = Observable.fromCallable(() -> {
            WebSocketDTO webSocketDTO = new WebSocketDTO("editMessage", new EditMessagePayload(messageId, newText));
            if (WebSocketConnection.isConnected()){
                if(chooseLayout.getMessageLayoutAttributes().getChatType().equals("Single"))
                    WebSocketConnection.send(OkHttpUtil.getUserUrl() + userId, webSocketDTO);
                else WebSocketConnection.send(OkHttpUtil.getChatUrl() + chatId, webSocketDTO);
                return "Успешно изменено";
            }
            else Log.w("WebSocket", "Соединение WebSocket не активно. Сообщение не удалено.");

            return "Не получилось изменить";
        });

        deleteDisposable = observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                result -> {
                    Log.d("EditDialog", "Результат: " + result);
                    chooseLayout.setTextInMessage(newText);
                },
                error -> Log.e("Изменение: исключение", "Текст исключения: " + error.getMessage(), error));
    }

    @Override
    public void onCancelEdit() {
        Toast.makeText(getContext(), "Действие по изменению отменено", Toast.LENGTH_SHORT).show();
    }

    private void showChooseActionDialog(){
        ChoseActionDialogFragment choseActionDialogFragment = new ChoseActionDialogFragment(this);
        choseActionDialogFragment.show(getChildFragmentManager(), "ChooseDialog");
    }

    private void showEditActionDialog(){
        EditMessageDialogFragment editMessageDialogFragment = new EditMessageDialogFragment(this, messageLayoutChooseText);
        editMessageDialogFragment.show(getChildFragmentManager(), "EditDialog");
    }

    private void showDeleteActionDialog(){
        ConfirmDeleteMessageDialogFragment confirmExitDialogFragment = new ConfirmDeleteMessageDialogFragment(this);
        confirmExitDialogFragment.show(getChildFragmentManager(), "DeleteDialog");
    }

    private void clearAllDialogFragment(){
        FragmentManager fragmentManager = getChildFragmentManager();
        List<Fragment> dialogFragments = fragmentManager.getFragments();
        for(Fragment fragment : dialogFragments){
            if(fragment instanceof DialogFragment dialogFragment){
                dialogFragment.dismiss();
            }
        }
    }

    private void deleteAction(){
        int messageId = chooseLayout.getId();
        Observable<String> observable = Observable.fromCallable(() -> {
            WebSocketDTO webSocketDTO = new WebSocketDTO("deleteMessage", messageId);
            if (WebSocketConnection.isConnected()){
                if(chooseLayout.getMessageLayoutAttributes().getChatType().equals("Single"))
                    WebSocketConnection.send(OkHttpUtil.getUserUrl() + userId, webSocketDTO);
                else WebSocketConnection.send(OkHttpUtil.getChatUrl() + chatId, webSocketDTO);
                return "Успешно удалено";
            }
            else Log.w("WebSocket", "Соединение WebSocket не активно. Сообщение не удалено.");

            return "Не получилось удалить";
        });

        deleteDisposable = observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                result -> {
                    Log.d("ConfirmDialog", "Результат: " + result);
                    chatFormLayout.removeView(chooseLayout);
                },
                error -> Log.e("Удаление: исключение", "Текст исключения: " + error.getMessage(), error));
    }
}