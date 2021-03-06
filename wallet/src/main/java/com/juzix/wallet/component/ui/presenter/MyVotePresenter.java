package com.juzix.wallet.component.ui.presenter;

import android.util.Log;

import com.juzhen.framework.util.MapUtils;
import com.juzhen.framework.util.NumberParserUtils;
import com.juzix.wallet.R;
import com.juzix.wallet.app.CustomThrowable;
import com.juzix.wallet.app.FlowableSchedulersTransformer;
import com.juzix.wallet.app.LoadingTransformer;
import com.juzix.wallet.app.SchedulersTransformer;
import com.juzix.wallet.component.ui.base.BasePresenter;
import com.juzix.wallet.component.ui.contract.MyVoteContract;
import com.juzix.wallet.component.ui.view.SubmitVoteActivity;
import com.juzix.wallet.engine.CandidateManager;
import com.juzix.wallet.engine.IndividualWalletManager;
import com.juzix.wallet.engine.VoteManager;
import com.juzix.wallet.entity.BatchVoteSummaryEntity;
import com.juzix.wallet.entity.BatchVoteTransactionEntity;
import com.juzix.wallet.entity.CandidateEntity;
import com.juzix.wallet.entity.IndividualWalletEntity;
import com.juzix.wallet.entity.VoteSummaryEntity;
import com.juzix.wallet.utils.BigDecimalUtil;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.SingleSource;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

/**
 * @author matrixelement
 */
public class MyVotePresenter extends BasePresenter<MyVoteContract.View> implements MyVoteContract.Presenter {

    private final static String TAG = MyVotePresenter.class.getSimpleName();
    private final static String TAG_LOCKED = "tag_locked";
    private final static String TAG_EARNINGS = "tag_earnings";
    private final static String TAG_INVALIDNUM = "tag_inValidNum";
    private final static String TAG_VALIDNUM = "tag_validNum";

    public MyVotePresenter(MyVoteContract.View view) {
        super(view);
    }

    @Override
    public void loadData() {

        List<String> walletAddressList = IndividualWalletManager.getInstance().getAddressList();

        getBatchVoteSummary(walletAddressList.toArray(new String[walletAddressList.size()]));

        getBatchVoteTransaction(walletAddressList.toArray(new String[walletAddressList.size()]));
    }

    @Override
    public void voteTicket(String candidateId) {

        if (isViewAttached()) {

            Flowable.just(IndividualWalletManager.getInstance().getWalletList())
                    .filter(new Predicate<ArrayList<IndividualWalletEntity>>() {
                        @Override
                        public boolean test(ArrayList<IndividualWalletEntity> individualWalletEntities) throws Exception {
                            return !individualWalletEntities.isEmpty();
                        }
                    })
                    .switchIfEmpty(new Flowable<ArrayList<IndividualWalletEntity>>() {
                        @Override
                        protected void subscribeActual(Subscriber<? super ArrayList<IndividualWalletEntity>> s) {
                            s.onError(new CustomThrowable(CustomThrowable.CODE_ERROR_NOT_EXIST_VALID_WALLET));
                        }
                    })
                    .flatMap(new Function<ArrayList<IndividualWalletEntity>, Publisher<IndividualWalletEntity>>() {
                        @Override
                        public Publisher<IndividualWalletEntity> apply(ArrayList<IndividualWalletEntity> individualWalletEntities) throws Exception {
                            return Flowable.fromIterable(individualWalletEntities);
                        }
                    })
                    .map(new Function<IndividualWalletEntity, Double>() {
                        @Override
                        public Double apply(IndividualWalletEntity individualWalletEntity) throws Exception {
                            return individualWalletEntity.getBalance();
                        }
                    })
                    .reduce(new BiFunction<Double, Double, Double>() {
                        @Override
                        public Double apply(Double aDouble, Double aDouble2) throws Exception {
                            return BigDecimalUtil.add(aDouble, aDouble2);
                        }
                    })
                    .filter(new Predicate<Double>() {
                        @Override
                        public boolean test(Double totalBalance) throws Exception {
                            return totalBalance > 0;
                        }
                    })
                    .switchIfEmpty(new Single<Double>() {
                        @Override
                        protected void subscribeActual(SingleObserver<? super Double> observer) {
                            observer.onError(new CustomThrowable(CustomThrowable.CODE_ERROR_VOTE_TICKET_INSUFFICIENT_BALANCE));
                        }
                    })
                    .flatMap(new Function<Double, SingleSource<CandidateEntity>>() {
                        @Override
                        public SingleSource<CandidateEntity> apply(Double aDouble) throws Exception {
                            return CandidateManager
                                    .getInstance()
                                    .getCandidateDetail(candidateId);
                        }
                    })
                    .compose(new SchedulersTransformer())
                    .compose(bindToLifecycle())
                    .compose(LoadingTransformer.bindToSingleLifecycle(currentActivity()))
                    .subscribe(new Consumer<CandidateEntity>() {
                        @Override
                        public void accept(CandidateEntity candidateEntity) throws Exception {
                            if (isViewAttached()) {
                                SubmitVoteActivity.actionStart(currentActivity(), candidateEntity);
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            if (throwable instanceof CustomThrowable) {
                                showLongToast(((CustomThrowable) throwable).getDetailMsgRes());
                            }
                        }
                    });

        }


    }

    private void getBatchVoteSummary(String[] addressList) {

        VoteManager.getInstance()
                .getBatchVoteSummary(addressList)
                .filter(new Predicate<List<BatchVoteSummaryEntity>>() {
                    @Override
                    public boolean test(List<BatchVoteSummaryEntity> batchVoteSummaryEntities) throws Exception {
                        return !batchVoteSummaryEntities.isEmpty();
                    }
                })
                .toFlowable()
                .flatMap(new Function<List<BatchVoteSummaryEntity>, Publisher<BatchVoteSummaryEntity>>() {
                    @Override
                    public Publisher<BatchVoteSummaryEntity> apply(List<BatchVoteSummaryEntity> batchVoteSummaryEntities) throws Exception {
                        return Flowable.fromIterable(batchVoteSummaryEntities);
                    }
                })
                .collect(new Callable<Map<String, Object>>() {
                    @Override
                    public Map<String, Object> call() throws Exception {
                        return new HashMap<>();
                    }
                }, new BiConsumer<Map<String, Object>, BatchVoteSummaryEntity>() {
                    @Override
                    public void accept(Map<String, Object> stringLongMap, BatchVoteSummaryEntity batchVoteSummaryEntity) throws Exception {
                        long invalidNum = NumberParserUtils.parseLong(batchVoteSummaryEntity.getTotalTicketNum()) - NumberParserUtils.parseLong(batchVoteSummaryEntity.getValidNum());
                        stringLongMap.put(TAG_LOCKED, NumberParserUtils.parseLong(batchVoteSummaryEntity.getLocked()) + MapUtils.getLong(stringLongMap, TAG_LOCKED));
                        stringLongMap.put(TAG_EARNINGS, NumberParserUtils.parseLong(batchVoteSummaryEntity.getEarnings()) + MapUtils.getLong(stringLongMap, TAG_EARNINGS));
                        stringLongMap.put(TAG_INVALIDNUM, invalidNum + MapUtils.getLong(stringLongMap, TAG_INVALIDNUM));
                        stringLongMap.put(TAG_VALIDNUM, NumberParserUtils.parseLong(batchVoteSummaryEntity.getValidNum()) + MapUtils.getLong(stringLongMap, TAG_VALIDNUM));
                    }
                })
                .map(new Function<Map<String, Object>, List<VoteSummaryEntity>>() {
                    @Override
                    public List<VoteSummaryEntity> apply(Map<String, Object> map) throws Exception {
                        return buildVoteSummaryList(map);
                    }
                })
                .compose(new SchedulersTransformer())
                .compose(bindToLifecycle())
                .subscribe(new Consumer<List<VoteSummaryEntity>>() {
                    @Override
                    public void accept(List<VoteSummaryEntity> voteSummaryEntityList) throws Exception {
                        if (isViewAttached()) {
                            getView().showBatchVoteSummary(voteSummaryEntityList);
                        }
                    }
                });

    }

    private void getBatchVoteTransaction(String[] addressList) {
        VoteManager.getInstance()
                .getBatchVoteTransaction(addressList)
                .toFlowable()
                .compose(new FlowableSchedulersTransformer())
                .compose(bindToLifecycle())
                .compose(LoadingTransformer.bindToFlowableLifecycle(currentActivity()))
                .subscribe(new Consumer<List<BatchVoteTransactionEntity>>() {
                    @Override
                    public void accept(List<BatchVoteTransactionEntity> batchVoteTransactionEntityList) throws Exception {
                        if (isViewAttached()) {
                            getView().showBatchVoteTransactionList(batchVoteTransactionEntityList);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e(TAG, throwable.getMessage());
                    }
                });
    }

    private List<VoteSummaryEntity> buildDefaultVoteSummaryList() {
        List<VoteSummaryEntity> voteSummaryEntityList = new ArrayList<>();
        voteSummaryEntityList.add(new VoteSummaryEntity(String.format("%s%s", string(R.string.lockVote), "(Energon)"), String.valueOf("-")));
        voteSummaryEntityList.add(new VoteSummaryEntity(String.format("%s%s", string(R.string.votingIncome), "(Energon)"), String.valueOf("-")));
        voteSummaryEntityList.add(new VoteSummaryEntity(String.format("%s", string(R.string.validInvalidTicket)), String.format("%d/%d", "-", "-")));
        return voteSummaryEntityList;
    }

    private List<VoteSummaryEntity> buildVoteSummaryList(Map<String, Object> map) {
        List<VoteSummaryEntity> voteSummaryEntityList = new ArrayList<>();
        if (map != null && !map.isEmpty()) {
            long locked = MapUtils.getLong(map, TAG_LOCKED);
            long earnings = MapUtils.getLong(map, TAG_EARNINGS);
            long invalidNum = MapUtils.getLong(map, TAG_INVALIDNUM);
            long validNum = MapUtils.getLong(map, TAG_VALIDNUM);

            voteSummaryEntityList.add(new VoteSummaryEntity(String.format("%s%s", string(R.string.lockVote), "(Energon)"), String.valueOf(locked)));
            voteSummaryEntityList.add(new VoteSummaryEntity(String.format("%s%s", string(R.string.votingIncome), "(Energon)"), String.valueOf(earnings)));
            voteSummaryEntityList.add(new VoteSummaryEntity(String.format("%s", string(R.string.validInvalidTicket)), String.format("%d/%d", validNum, invalidNum)));
        }

        return voteSummaryEntityList;
    }
}
