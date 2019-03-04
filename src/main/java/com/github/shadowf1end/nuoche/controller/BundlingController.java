package com.github.shadowf1end.nuoche.controller;

import cn.binarywang.wx.miniapp.api.WxMaMsgService;
import cn.binarywang.wx.miniapp.bean.WxMaTemplateData;
import cn.binarywang.wx.miniapp.bean.WxMaTemplateMessage;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.dyvmsapi.model.v20170525.SingleCallByTtsRequest;
import com.aliyuncs.dyvmsapi.model.v20170525.SingleCallByTtsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.github.shadowf1end.nuoche.base.BaseController;
import com.github.shadowf1end.nuoche.common.util.RedisUtil;
import com.github.shadowf1end.nuoche.common.util.ResultUtil;
import com.github.shadowf1end.nuoche.common.vo.Result;
import com.github.shadowf1end.nuoche.entity.Bundling;
import com.github.shadowf1end.nuoche.entity.ClaimPoint;
import com.github.shadowf1end.nuoche.entity.VoiceRecord;
import com.github.shadowf1end.nuoche.entity.WechatUser;
import com.github.shadowf1end.nuoche.service.*;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author su
 */
@RestController
@RequestMapping("bundling")
public class BundlingController extends BaseController {

    private final HttpServletRequest request;
    private final BundlingService bundlingService;
    private final RedisUtil redisUtil;
    private final ClaimPointService claimPointService;
    private final VoiceRecordService voiceRecordService;
    private final WechatUserService wechatUserService;
    private final WxMaMsgService wxMaMsgService;
    private final PartnerService partnerService;

    @Autowired
    public BundlingController(HttpServletRequest request, BundlingService bundlingService, RedisUtil redisUtil, ClaimPointService claimPointService, VoiceRecordService voiceRecordService, WechatUserService wechatUserService, WxMaMsgService wxMaMsgService, PartnerService partnerService) {
        this.request = request;
        this.bundlingService = bundlingService;
        this.redisUtil = redisUtil;
        this.claimPointService = claimPointService;
        this.voiceRecordService = voiceRecordService;
        this.wechatUserService = wechatUserService;
        this.wxMaMsgService = wxMaMsgService;
        this.partnerService = partnerService;
    }

    /**
     * 发送验证码
     * @param phone
     * @return
     * @throws ClientException
     */
    @RequestMapping(value = "captcha", method = RequestMethod.POST)
    public Result<Object> captcha(String phone) throws ClientException {
        String captcha = generateCaptcha(5);

//        // 短信应用SDK AppID
//        int appid = 1400162243;
//        // 短信应用SDK AppKey
//        String appkey = "ad6c726338e59cc2027896bdadeeb739";
//        // 短信模板ID，需要在短信应用中申请
//        int templateId = 239764;
//        String[] params = {captcha, "15"};
//        SmsSingleSender ssender = new SmsSingleSender(appid, appkey);
//        SmsSingleSenderResult result = ssender.sendWithParam("86", phone, templateId, params, "", "", "");

        //设置超时时间-可自行调整
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");
        //初始化ascClient需要的几个参数
        final String product = "Dysmsapi";//短信API产品名称（短信产品名固定，无需修改）
        final String domain = "dysmsapi.aliyuncs.com";//短信API产品域名（接口地址固定，无需修改）
        //替换成你的AK
        final String accessKeyId = "LTAIuUku2aFnC5mA";//你的accessKeyId,参考本文档步骤2
        final String accessKeySecret = "cL0QjAOI72rPN20FxJgLadRkCvh7lX";//你的accessKeySecret，参考本文档步骤2
        //初始化ascClient,暂时不支持多region（请勿修改）
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId,
                accessKeySecret);
        DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
        IAcsClient acsClient = new DefaultAcsClient(profile);
        //组装请求对象
        SendSmsRequest request = new SendSmsRequest();
        //使用post提交
        request.setMethod(MethodType.POST);
        //必填:待发送手机号。支持以逗号分隔的形式进行批量调用，批量上限为1000个手机号码,批量调用相对于单条调用及时性稍有延迟,验证码类型的短信推荐使用单条调用的方式；发送国际/港澳台消息时，接收号码格式为国际区号+号码，如“85200000000”
        request.setPhoneNumbers(phone);
        //必填:短信签名-可在短信控制台中找到
        request.setSignName("挪车贴");
        //必填:短信模板-可在短信控制台中找到，发送国际/港澳台消息时，请使用国际/港澳台短信模版
        request.setTemplateCode("SMS_152512975");
        //可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
        //友情提示:如果JSON中需要带换行符,请参照标准的JSON协议对换行符的要求,比如短信内容中包含\r\n的情况在JSON中需要表示成\\r\\n,否则会导致JSON在服务端解析失败
        request.setTemplateParam("{\"code\":\"" + captcha + "\"}");
        //可选-上行短信扩展码(扩展码字段控制在7位或以下，无特殊需求用户请忽略此字段)
        //request.setSmsUpExtendCode("90997");
        //可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
//        request.setOutId("yourOutId");
        //请求失败这里会抛ClientException异常
        SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);
        if (sendSmsResponse.getCode() != null && sendSmsResponse.getCode().equals("OK")) {
            //请求成功
            redisUtil.set(phone, captcha);
            redisUtil.expireAt(phone, new Date(System.currentTimeMillis() + 16 * 60 * 1000));
            return new ResultUtil<>().setSuccessMsg("发送成功");
        } else {
            return new ResultUtil<>().setErrorMsg(sendSmsResponse.getMessage());
        }
    }

    /**
     * 激活挪车贴
     * @param id
     * @param phone
     * @param plate
     * @param captcha
     * @return
     */
    @RequestMapping(value = "activate", method = RequestMethod.POST)
    public Result<Object> activate(Long id, String phone, String plate, String captcha) {
        String cacheCaptcha = redisUtil.get(phone);
        if (cacheCaptcha == null || !cacheCaptcha.equals(captcha)) {
            return new ResultUtil<>().setErrorMsg("验证码错误");
        }

        Long wechatUserId = getUserId(request);
        Bundling bundling = bundlingService.find(id);
        bundling.setPhone(phone);
        bundling.setPlate(plate);
        WechatUser wechatUser = wechatUserService.find(wechatUserId);
        bundling.setNickName(wechatUser.getNickName());
        bundling.setWechatUserId(wechatUserId);
        bundlingService.save(bundling);
        if (wechatUser.getRecId() == null && bundling.getPartnerId() != null) {
            wechatUser.setRecId(partnerService.find(bundling.getPartnerId()).getWechatUserId());
            wechatUserService.save(wechatUser);
        }
        return new ResultUtil<>().setSuccessMsg("激活成功");
    }

    /**
     * 挪车贴绑定状态，1：已绑定，0：未绑定
     * @param id
     * @return
     */
    @RequestMapping(value = "state", method = RequestMethod.POST)
    public Result<Object> state(Long id) {
        Bundling bundling = bundlingService.find(id);
        bundling.setQrCode(null);
        ClaimPoint claimPoint = claimPointService.find(bundling.getClaimPointId());
        Map<String, Object> map = new HashMap<>(3);
        map.put("bundling", bundling);
        map.put("claimPoint", claimPoint);
        if (bundling.getWechatUserId() != null) {
            map.put("state", 1);
        } else {
            map.put("state", 0);
        }
        return new ResultUtil<>().setData(map);
    }

    /**
     * 获取用户挪车贴二维码列表
     * @return
     */
    @RequestMapping(value = "qrCode", method = RequestMethod.GET)
    public Result<Object> qrCode() {
        List<Bundling> bundlings = bundlingService.listByWechatUserId(getUserId(request));
        bundlings.forEach(bundling -> {
            bundling.setClaimPointId(null);
            bundling.setWechatUserId(null);
            bundling.setId(null);
        });
        return new ResultUtil<>().setData(bundlings);
    }

    /**
     * 单个领取点下激活用户信息
     * @param claimPointId
     * @param page
     * @param size
     * @return
     */
    @RequestMapping(value = "list", method = RequestMethod.POST)
    public Result<Object> list(Long claimPointId, Integer page, Integer size) {
        if (page == null || size == null) {
            page = 0;
            size = 20;
        }
        List<Bundling> bundlings = bundlingService.listByClaimPointId(claimPointId, page, size);
        bundlings.forEach(bundling -> {
            bundling.setQrCode(null);
            bundling.setPhone(null);
        });
        return new ResultUtil<>().setData(bundlings);
    }

    /**
     * 通知挪车
     * @param id
     * @return
     * @throws ClientException
     */
    @RequestMapping(value = "notice", method = RequestMethod.POST)
    public Result<Object> notice(Long id) throws ClientException {
        Bundling bundling;
        try {
            bundling = bundlingService.find(id);
        } catch (Exception e) {
            return new ResultUtil<>().setErrorMsg("无此挪车贴");
        }

        if (bundling.getWechatUserId().equals(getUserId(request))) {
            return new ResultUtil<>().setErrorMsg("不能提醒自己");
        }
//        // 短信应用SDK AppID
//        int appid = 1400162243;
//        // 短信应用SDK AppKey
//        String appkey = "ad6c726338e59cc2027896bdadeeb739";
//        int templateId = 239854;

        ClaimPoint claimPoint = claimPointService.find(bundling.getClaimPointId());

//        String[] params = {claimPoint.getName()};
//        TtsVoiceSender tvsender = new TtsVoiceSender(appid, appkey);
//        TtsVoiceSenderResult result = tvsender.send("86", bundling.getPhone(), templateId, params, 2, "");

        //设置访问超时时间
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");
        //云通信产品-语音API服务产品名称（产品名固定，无需修改）
        final String product = "Dyvmsapi";
        //产品域名（接口地址固定，无需修改）
        final String domain = "dyvmsapi.aliyuncs.com";
        //AK信息
        final String accessKeyId = "LTAIuUku2aFnC5mA";
        final String accessKeySecret = "cL0QjAOI72rPN20FxJgLadRkCvh7lX";
        //初始化acsClient 暂时不支持多region
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
        DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
        IAcsClient acsClient = new DefaultAcsClient(profile);
        SingleCallByTtsRequest singleCallByTtsRequest = new SingleCallByTtsRequest();
        //必填-被叫显号,可在语音控制台中找到所购买的显号
        singleCallByTtsRequest.setCalledShowNumber("01086391333");
        //必填-被叫号码
        singleCallByTtsRequest.setCalledNumber(bundling.getPhone());
        String msg = claimPoint.getDetail();
        String name = claimPoint.getName();
        if(name == null) {
            name = "挪车贴";
        }
        if (msg != null && !"".equals(msg)) {
            //必填-Tts模板ID
            singleCallByTtsRequest.setTtsCode("TTS_153880938");
            //可选-当模板中存在变量时需要设置此值
            singleCallByTtsRequest.setTtsParam("{\"name\":\"" + name + "\",\"msg\":\"最新优惠活动：" + msg + "\"}");
        } else { //必填-Tts模板ID
            singleCallByTtsRequest.setTtsCode("TTS_152508045");
            //可选-当模板中存在变量时需要设置此值
            singleCallByTtsRequest.setTtsParam("{\"name\":\"" + name + "\"}");
        }
        //可选-音量 取值范围 0--200
        singleCallByTtsRequest.setVolume(100);
        //可选-播放次数
        singleCallByTtsRequest.setPlayTimes(3);
        //可选-外部扩展字段,此ID将在回执消息中带回给调用方
//        request.setOutId("yourOutId");
        //hint 此处可能会抛出异常，注意catch
        SingleCallByTtsResponse singleCallByTtsResponse = acsClient.getAcsResponse(singleCallByTtsRequest);

        if ("OK".equals(singleCallByTtsResponse.getCode())) {
            VoiceRecord voiceRecord = new VoiceRecord();
            voiceRecord.setMessage(singleCallByTtsResponse.getMessage());
            voiceRecord.setPhone(bundling.getPhone());
            voiceRecord.setWechatUserId(getUserId(request));
            voiceRecord.setResult(singleCallByTtsResponse.getCode());
            voiceRecordService.save(voiceRecord);

            Boolean flag = false;
            if (msg != null && !"".equals(msg)) {
                flag = true;
                msg = "（" + name + "最新优惠活动：" + msg + "）";
            }

            //请求成功
            String requestFormId = wechatUserService.getFormId(getUserId(request));
            if (requestFormId != null && !"".equals(requestFormId)) {
                WechatUser requestUser = wechatUserService.find(getUserId(request));
                if (flag) {
                    msg = "（" + "不好意思，马上过来。" + msg + "）";
                }
                try {
                    sendRequestTemplateMessage(requestFormId, requestUser.getOpenId(), bundling.getPlate(), msg);
                } catch (WxErrorException e) {
                    e.printStackTrace();
                }
            }

            String receiveFormId = wechatUserService.getFormId(bundling.getWechatUserId());
            if (receiveFormId != null && !"".equals(receiveFormId)) {
                WechatUser receiveUser = wechatUserService.find(bundling.getWechatUserId());
                if (flag) {
                    msg = "您的爱车阻挡道路，请尽快处理，谢谢您的配合！" + msg;
                }
                try {
                    sendReceiveTemplateMessage(receiveFormId, receiveUser.getOpenId(), bundling.getPlate(), msg);
                } catch (WxErrorException e) {
                    e.printStackTrace();
                }
            }
            return new ResultUtil<>().setSuccessMsg("提醒成功");
        } else {
            return new ResultUtil<>().setErrorMsg(singleCallByTtsResponse.getMessage());
        }
    }

    /**
     * 单个领取点激活数量及总数量
     * @param claimPointId
     * @return
     */
    @RequestMapping(value = "count", method = RequestMethod.POST)
    public Result<Object> count(Long claimPointId) {
        Map<String, Integer> map = new HashMap<String, Integer>(2) {
            {
                put("activated", bundlingService.countByClaimPointIdAndWechatUserIdIsNotNull(claimPointId));
                put("sum", bundlingService.countByClaimPointId(claimPointId));
            }
        };
        return new ResultUtil<>().setData(map);
    }

    private void sendRequestTemplateMessage(String formId, String openId, String plate, String msg) throws WxErrorException {
        WxMaTemplateMessage wxMaTemplateMessage = new WxMaTemplateMessage();
        wxMaTemplateMessage.setFormId(formId);
        wxMaTemplateMessage.setToUser(openId);
        wxMaTemplateMessage.setTemplateId("ylT0vWJz7foW7CTqFE2BWwjEiZFpJItgN4t3SvXJkJw");
        wxMaTemplateMessage.setPage("pages/Index");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        String prompt = "已成功通知到" + plate + "的车主，请耐心等候！";
        List<WxMaTemplateData> list = new ArrayList<>();
        list.add(new WxMaTemplateData("keyword1", "语音"));
        list.add(new WxMaTemplateData("keyword2", sdf.format(new Date())));
        list.add(new WxMaTemplateData("keyword3", prompt));
        list.add(new WxMaTemplateData("keyword4", msg));
        wxMaTemplateMessage.setData(list);
        wxMaMsgService.sendTemplateMsg(wxMaTemplateMessage);
    }

    private void sendReceiveTemplateMessage(String formId, String openId, String plate, String msg) throws WxErrorException {
        WxMaTemplateMessage wxMaTemplateMessage = new WxMaTemplateMessage();
        wxMaTemplateMessage.setFormId(formId);
        wxMaTemplateMessage.setToUser(openId);
        wxMaTemplateMessage.setTemplateId("MQEM8PSMEI_QZJ70OzLI1dgQ5faC9JB1tBc0TA2lnRM");
        wxMaTemplateMessage.setEmphasisKeyword("keyword1.DATA");
        wxMaTemplateMessage.setPage("pages/Index");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        String prompt = "详询0357-8678910";
        List<WxMaTemplateData> list = new ArrayList<>();
        list.add(new WxMaTemplateData("keyword1", plate));
        list.add(new WxMaTemplateData("keyword2", msg));
        list.add(new WxMaTemplateData("keyword3", sdf.format(new Date())));
        list.add(new WxMaTemplateData("keyword4", prompt));
        wxMaTemplateMessage.setData(list);
        wxMaMsgService.sendTemplateMsg(wxMaTemplateMessage);
    }

    private String generateCaptcha(int length) {
        String s = "0123456789";
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            stringBuilder.append(s.charAt(random.nextInt(10)));
        }
        return stringBuilder.toString();
    }
}