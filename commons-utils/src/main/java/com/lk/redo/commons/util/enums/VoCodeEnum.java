package com.lk.redo.commons.util.enums;


import com.lk.redo.commons.util.utils.EnumUtil;

/**
 * Created on 2018/1/19 10:46
 * <p>
 * Description: [返回code对照表]
 * 1000-1999为商品模块code码<br/>
 *  2000-2999为产品模块code码
 * <p>
 * Company: [尚德机构]
 *
 * @author [刘辉]
 */
public enum VoCodeEnum {
    /**
     * 请求成功
     */
    SUCCESS("200","请求成功"),
    FAIL("400", "请求失败"),
    ILLEGAL_ARGUMENTS("401","参数异常"),
    MISS_ARGUMENTS("402","缺少必填参数"),
    REQUEST_TIMEOUT("501","服务器处理超时"),

    INSURANCE_INSERT_FAIL("1000","新增保单失败"),
    INSURANCE_AMOUNT_ERROR("1002","保险金额有误"),
    INSURANCE_UPDATE_FAIL("1003","更新保单失败"),
    NOT_FOUND_INSURANCE_DETAIL("1004","未配置保险信息"),

    FAIL_INSERT_ORDER_INSURANCE_DETAIL("1005","订单创建消息保存保费明细失败"),
    NOT_FOUND_ORDER_INSURANCE_DETAIL("1006","未找到订单保费明细"),
    FAIL_FOUND_ORDER_INSURANCE_DETAIL("1007","查询订单保费明细失败"),

    FAIL_UPDATE_ORDER_INSURANCE("1008","更新保单失败"),
    FAIL_QUERY_ORDER_INSURANCE("1009","查询保单失败"),

    FAIL_CALCULATE_PREMIUM("1010","计算保费失败"),
    FAIL_INSERT_INSURANCE_DETAIL_PREMIUM("1011","新增子单保费明细失败"),
    FAIL_UPDATE_INSURANCE_DETAIL_PREMIUM("1012","更新子单保费明细失败"),

    FAIL_PRO_CHANGED_INSURANCE("1013","处理转班失败"),
    FAIL_ENDORSEMENT_INSURANCE_COMPANY("1014","调用保司处理批改失败"),

    FAIL_CREATE_NO_PASS_REFUND("1015","创建不过退费险失败"),
    FAIL_UPDAET_NO_PASS_REFUND("1016","修改不过退费险失败"),

    FAIL_ENDORSEMENT_INSURANCE("1017","处理批改失败"),

    FAIL_UPDATE_CLASS_INSURANCE_COMPANY("1018","调用保司处理转班失败"),
    FAIL_QUERY_PACKAGE_SUBJECT("1019","查询产品包科目失败"),

    FAIL_CONSUMER_CREATE_ORDER_MESSAGE("1020","下单消息消费失败"),
    FAIL_QUERY_USER_INFO("1021","查询用户信息失败"),
    QUERY_SIZE_LIMIT("1022","查询数量超过限制"),
    REFUND_SUCCESS_MQ_FAIL("1023","消费成功的退费消息失败"),
    REFUND_FAIL_MQ_FAIL("1024","消费失败的退费消息失败"),

    FAIL_CANCEL_INSURANCE_COMPANY("1025","调用保司处理解约失败"),

    FAIL_QUERY_ITEM_INFO("1026","查询商品信息失败"),

    FAIL_QUERY_ORDER_INFO("1027","查询订单信息失败"),

    FAIL_QUERY_ORDER_REFUND_AMONUT("1029","查询子单可退金额失败"),

    ERROR_QUERY_USER_INFO("1028","查询用户信息失败"),

    NOT_FOUND_PACKEGE_INSURANCE("2001", "产品包id没有查到保单信息,请确认有可用保单"),
    PACKEGE_INSURANCE_MORE_THEN_ONE("2002", "根据产品包id查询到多条生效保单记录"),
    UPDATE_INSUNANCE_REFUND_FAIL("2003", "根据refundId更新退保记录失败"),

    PRICEG_GRADE_BOUNDARY_COINCIDENCE("3001","价格区间边界不能重叠"),
    PRICEG_GRADE_BOUNDARY_NEGATIVE("3002","价格区间边界不能是负数"),
    PRICEG_GRADE_BOUNDARY_IS_NULL("3003","价格区间边界不能为空"),
    PRICEG_GRADE_BOUNDARYS_IS_EQUAL("3004","价格区间边界上下界不能相等"),
    PRICE_RATE_IS_NEGATIVE("3005","费率必须大于0"),
    PRICE_RATE_OUT_OF_BOUND("3006","费率不能超过100"),


    FAIL_APPLY_CLAIM("4001","报案失败")



    ;

    private String code;
    private String msg;

    VoCodeEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
        EnumUtil.putEnum(code, this);
    }

    public static VoCodeEnum getByCode(String code) {
        return EnumUtil.getEnum(VoCodeEnum.class, code);
    }
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "VoCodeEnum{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                '}';
    }

    public static String toDescString() {
        StringBuilder str = new StringBuilder();
        str.append("枚举名|code码|描述\n- | :-: | -: \n");
        for (VoCodeEnum voCodeEnum : VoCodeEnum.values()) {
            str.append(voCodeEnum.name()).append("|").append(voCodeEnum.getCode()).append("|").append(voCodeEnum.getMsg()).append("\n");
        }
        return str.toString();
    }

    public static void main(String[] args) {
        System.out.println(VoCodeEnum.toDescString());
    }
}
