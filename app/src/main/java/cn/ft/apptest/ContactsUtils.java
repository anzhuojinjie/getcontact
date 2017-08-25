package cn.ft.apptest;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Im;
import android.provider.ContactsContract.CommonDataKinds.Nickname;
import android.provider.ContactsContract.CommonDataKinds.Note;
import android.provider.ContactsContract.CommonDataKinds.Organization;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.CommonDataKinds.StructuredPostal;
import android.provider.ContactsContract.Data;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Desction:联系人工具
 * Author:pengjianbo
 * Date:16/7/14 下午4:15
 */
class ContactsUtils {

    private static List<ContactsBean> getContactBeanByContactId(List<ContactsBean> list, int contactId) {
        List<ContactsBean> mobileContactList = new ArrayList<>();
        for(ContactsBean bean:list){
            if(bean.getContactId() == contactId){
                mobileContactList.add(bean);
            }
        }
        return mobileContactList;
    }
    /**
     * 读取联系人
     * 所需权限android.permission.READ_CONTACTS,会弹出授权框
     * @param context
     * @return
     */
    public static List<ContactsBean> getAllContact(Context context) {
        Cursor cursor = context.getContentResolver().query(Data.CONTENT_URI, null, null, null, ContactsContract.Data.RAW_CONTACT_ID);
        if (cursor == null) {
            return null;
        }

        List<ContactsBean> contactBeanList = new ArrayList<>();
        while (cursor.moveToNext()) {
            try {
                int contactId = cursor.getInt(cursor.getColumnIndex(ContactsContract.Data.RAW_CONTACT_ID));

                List<ContactsBean> curContactList = getContactBeanByContactId(contactBeanList, contactId);
                if (curContactList.size() == 0) {
                    ContactsBean bean = new ContactsBean();
                    bean.setContactId(contactId);

                    contactBeanList.add(bean);
                    curContactList.add(bean);
                }

                String mimeType = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.MIMETYPE));
                if (TextUtils.equals(mimeType, StructuredName.CONTENT_ITEM_TYPE)) {//姓名
                    //姓氏
                    String firstName = cursor.getString(cursor.getColumnIndex(StructuredName.FAMILY_NAME));
                    //名字
                    String lastName = cursor.getString(cursor.getColumnIndex(StructuredName.GIVEN_NAME));
                    for (ContactsBean bean : curContactList) {
                        StringBuffer nameSb = new StringBuffer();
                        if (!TextUtils.isEmpty(firstName)) {
                            nameSb.append(firstName);
                        }
                        if (!TextUtils.isEmpty(lastName)) {
                            nameSb.append(lastName);
                        }
                        bean.setName(nameSb.toString());
                    }
                } else if (TextUtils.equals(mimeType, Phone.CONTENT_ITEM_TYPE)) {//手机号
                    String mobile = cursor.getString(cursor.getColumnIndex(Phone.NUMBER));
                    if (TextUtils.isEmpty(mobile)) {
                        continue;
                    }
                    boolean hasMobile = false;
                    mobile = mobile.replace("-", "");
                    Pattern p = Pattern.compile("\\s*|t|r|n");
                    Matcher m = p.matcher(mobile);
                    mobile = m.replaceAll("");

                    for (ContactsBean bean : curContactList) {
                        if ((!TextUtils.isEmpty(bean.getMobile())) && TextUtils.equals(bean.getMobile(), mobile)) {
                            hasMobile = true;
                        }
                    }
                    if (!hasMobile && curContactList.size() > 0) {
                        ContactsBean bean = curContactList.get(0);
                        if (TextUtils.isEmpty(bean.getMobile())) {
                            bean.setMobile(mobile);
                        } else {
                            bean = bean.clone();
                            bean.setMobile(mobile);
                            contactBeanList.add(bean);
                            curContactList.add(bean);
                        }
                    }

                } else if (TextUtils.equals(mimeType, Email.CONTENT_ITEM_TYPE)) {//E-MAIL
                    String email;
                    if (Build.VERSION.SDK_INT >= 11) {
                        email = cursor.getString(cursor.getColumnIndex(Email.ADDRESS));
                    } else {
                        email = cursor.getString(cursor.getColumnIndex("data1"));
                    }
                    for (ContactsBean bean : curContactList) {
                        bean.setEmail(email);
                    }
                } else if (TextUtils.equals(mimeType, Organization.CONTENT_ITEM_TYPE)) {//组织
                    String company = cursor.getString(cursor.getColumnIndex(Organization.COMPANY));
                    String department = cursor.getString(cursor.getColumnIndex(Organization.DEPARTMENT));
                    String position = cursor.getString(cursor.getColumnIndex(Organization.TITLE));
                    for (ContactsBean bean : curContactList) {
                        bean.setCompany(company);
                        bean.setDepartment(department);
                        bean.setPosition(position);
                    }
                } else if (TextUtils.equals(mimeType, Im.CONTENT_ITEM_TYPE)) {//IM
                    int protocal = cursor.getInt(cursor.getColumnIndex(Im.PROTOCOL));
                    //QQ
                    if (protocal == Im.PROTOCOL_QQ) {
                        String qq = cursor.getString(cursor.getColumnIndex(Im.DATA));
                        for (ContactsBean bean : curContactList) {
                            bean.setQq(qq);
                        }
                    }
                } else if (TextUtils.equals(mimeType, Nickname.CONTENT_ITEM_TYPE)) {//昵称
                    String nickName = cursor.getString(cursor.getColumnIndex(Nickname.NAME));
                    for (ContactsBean bean : curContactList) {
                        bean.setNickName(nickName);
                    }
                } else if (TextUtils.equals(mimeType, Note.CONTENT_ITEM_TYPE)) {//备注
                    String note = cursor.getString(cursor.getColumnIndex(Note.NOTE));
                    for (ContactsBean bean : curContactList) {
                        bean.setNote(note);
                    }
                } else if (TextUtils.equals(mimeType, StructuredPostal.CONTENT_ITEM_TYPE)) {//地址
                    String address = cursor.getString(cursor.getColumnIndex(StructuredPostal.FORMATTED_ADDRESS));
                    for (ContactsBean bean : curContactList) {
                        bean.setAddress(address);
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        cursor.close();

        return contactBeanList;
    }
    /**
     * 获取通话记录
     * 所需权限:android.permission.READ_CALL_LOG,会弹出授权框
     * @param context
     * @return
     */
    public static List<CallRecordsBean> getCallRecords(Context context) {
        if (PackageManager.PERMISSION_GRANTED == context.checkPermission(Manifest.permission.READ_CALL_LOG, android.os.Process.myPid(), android.os.Process.myUid())) {
            Cursor cursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, CallLog.Calls.DATE + " desc");
            if (cursor == null) {
                return null;
            }

            List<CallRecordsBean> callRecordList = new ArrayList<>();
            while (cursor.moveToNext()) {
                try {
                    CallRecordsBean bean = new CallRecordsBean();
                    //号码
                    String mobile = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));

                    bean.setMobile(mobile);
                    //呼叫类型
                    String type;
                    switch (Integer.parseInt(cursor.getString(cursor.getColumnIndex(CallLog.Calls.TYPE)))) {
                        case CallLog.Calls.INCOMING_TYPE:
                            type = "呼入";
                            break;
                        case CallLog.Calls.OUTGOING_TYPE:
                            type = "呼出";
                            break;
                        case CallLog.Calls.MISSED_TYPE:
                            type = "未接";
                            break;
                        default:
                            type = "挂断";//应该是挂断.根据我手机类型判断出的
                            break;
                    }
                    bean.setType(type);

                    //开始通话时间
                    String callTime = cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.DATE));
                    bean.setCallTime(callTime);
                    //联系人
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.CACHED_NAME));
                    bean.setName(name);

                    //通话时间,单位:s
                    String duration = cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.DURATION));
                    bean.setDuration(duration);

                    callRecordList.add(bean);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            cursor.close();

            return callRecordList;
        }

        return null;
    }
}
