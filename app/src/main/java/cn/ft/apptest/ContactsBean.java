package cn.ft.apptest;

import android.text.TextUtils;

/**
 * Desction:
 * Author:pengjianbo
 * Date:2017/1/15 AM5:01
 */
class ContactsBean implements Cloneable{
    private int contactId;//通信录ID
    private String name;//姓名
    private String mobile;//手机号码
    private String email;//邮箱

    private String company;//公司
    private String department;//部门
    private String position;//职位
    private String address;//地址
    private String qq;//QQ
    private String nickName;//昵称
    private String note;//备注

    public int getContactId() {
        return contactId;
    }

    void setContactId(int contactId) {
        this.contactId = contactId;
    }

    public String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    void setEmail(String email) {
        this.email = email;
    }

    public String getCompany() {
        return company;
    }

    void setCompany(String company) {
        this.company = company;
    }

    public String getDepartment() {
        return department;
    }

    void setDepartment(String department) {
        this.department = department;
    }

    public String getPosition() {
        return position;
    }

    void setPosition(String position) {
        this.position = position;
    }

    public String getQq() {
        return qq;
    }

    void setQq(String qq) {
        this.qq = qq;
    }

    public String getNickName() {
        return nickName;
    }

    void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getNote() {
        return note;
    }

    void setNote(String note) {
        this.note = note;
    }

    public String getAddress() {
        return address;
    }

    void setAddress(String address) {
        this.address = address;
    }

    @Override
    public boolean equals(Object o) {
        if(o == null || !(o instanceof ContactsBean)) {
            return false;
        }
        ContactsBean bean = (ContactsBean) o;
        if(bean == null) {
            return false;
        }
        if(TextUtils.equals(name, bean.getName())
                && TextUtils.equals(mobile, bean.getMobile())) {
            return true;
        }

        return false;
    }

    @Override
    public ContactsBean clone() {
        ContactsBean o = null;
        try {
            o = (ContactsBean) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return o;
    }
}
