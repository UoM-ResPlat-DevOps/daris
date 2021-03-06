package daris.client.ui.secure.wallet;

import arc.gui.ValidatedInterfaceComponent;
import arc.gui.dialog.DialogProperties;
import arc.gui.form.Field;
import arc.gui.form.FieldDefinition;
import arc.gui.form.Form;
import arc.gui.form.FormItem;
import arc.gui.form.FormItem.Property;
import arc.gui.form.FormItemListener;
import arc.gui.gwt.colour.RGB;
import arc.gui.gwt.widget.HTML;
import arc.gui.gwt.widget.dialog.Dialog;
import arc.gui.gwt.widget.panel.VerticalPanel;
import arc.mf.client.util.ActionListener;
import arc.mf.client.util.AsynchronousAction;
import arc.mf.client.util.Validity;
import arc.mf.dtype.PasswordType;
import arc.mf.object.Null;
import arc.mf.object.ObjectMessageResponse;

import com.google.gwt.user.client.ui.Widget;

import daris.client.model.secure.wallet.SecureWallet;

public class SecureWalletPasswordSetForm extends ValidatedInterfaceComponent implements AsynchronousAction {

    private VerticalPanel _vp;
    private String _oldPassword;
    private String _password;

    private HTML _sb;

    public SecureWalletPasswordSetForm() {
        _vp = new VerticalPanel();
        _vp.fitToParent();

        Form form = new Form();
        form.setMargin(25);
        form.fitToParent();
        
        Field<String> oldPassword = new Field<String>(new FieldDefinition("old password", PasswordType.DEFAULT,
                "User's previous login password.", "User's previous login password.", 1, 1));
        oldPassword.addListener(new FormItemListener<String>() {

            @Override
            public void itemValueChanged(FormItem<String> f) {
                _oldPassword = f.value();
            }

            @Override
            public void itemPropertyChanged(FormItem<String> f, Property property) {

            }
        });
        form.add(oldPassword);

        Field<String> password = new Field<String>(new FieldDefinition("password", PasswordType.DEFAULT,
                "User's current login password.", "User's current login password.", 1, 1));
        password.addListener(new FormItemListener<String>() {

            @Override
            public void itemValueChanged(FormItem<String> f) {
                _password = f.value();
            }

            @Override
            public void itemPropertyChanged(FormItem<String> f, Property property) {

            }
        });
        form.add(password);

        addMustBeValid(form);

        form.render();
        _vp.add(form);

        _sb = new HTML();
        _sb.setHeight(22);
        _sb.setFontSize(11);
        _sb.setColour(RGB.RED);
        _sb.setPaddingLeft(25);
        _vp.add(_sb);

    }

    @Override
    public Validity valid() {
        Validity v = super.valid();
        if (v.valid()) {
            _sb.clear();
        } else {
            _sb.setHTML(v.reasonForIssue());
        }
        return v;
    }

    @Override
    public Widget gui() {
        return _vp;
    }

    @Override
    public void execute(final ActionListener l) {

        SecureWallet.setPassword(_oldPassword, _password, new ObjectMessageResponse<Null>() {
            @Override
            public void responded(Null r) {
                if (l != null) {
                    l.executed(true);
                }
            }
        });
    }

    public void showDialog(arc.gui.window.Window owner, ActionListener al) {

        DialogProperties dp = new DialogProperties(DialogProperties.Type.ACTION,
                "Update password to recrypt secure wallet", this);
        dp.setActionEnabled(false);
        dp.setButtonAction(this);
        dp.setButtonLabel("Update");
        dp.setCancelLabel("Cancel");
        dp.setOwner(owner);
        dp.setModal(true);
        dp.setSize(390, 200);

        Dialog dialog = Dialog.postDialog(dp, al);
        dialog.show();
    }

}
