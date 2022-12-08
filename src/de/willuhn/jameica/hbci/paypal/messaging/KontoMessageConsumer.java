/**********************************************************************
 *
 * Copyright (c) 2022 Olaf Willuhn
 * All rights reserved.
 * 
 * This software is copyrighted work licensed under the terms of the
 * Jameica License.  Please consult the file "LICENSE" for details. 
 *
 **********************************************************************/

package de.willuhn.jameica.hbci.paypal.messaging;

import de.willuhn.datasource.GenericObject;
import de.willuhn.jameica.hbci.messaging.SaldoMessage;
import de.willuhn.jameica.hbci.paypal.Plugin;
import de.willuhn.jameica.hbci.paypal.SupportStatus;
import de.willuhn.jameica.hbci.paypal.gui.action.SetupPaypalStep1;
import de.willuhn.jameica.hbci.rmi.Konto;
import de.willuhn.jameica.messaging.Message;
import de.willuhn.jameica.messaging.MessageConsumer;
import de.willuhn.jameica.system.Application;
import de.willuhn.util.I18N;

/**
 * Wird benachrichtigt, wenn ein Konto gespeichert wird und prueft, ob der Paypal-Wizzard gestartet werden muss.
 */
public class KontoMessageConsumer implements MessageConsumer
{
  private final static I18N i18n = Application.getPluginLoader().getPlugin(Plugin.class).getResources().getI18N();
  
  /**
   * @see de.willuhn.jameica.messaging.MessageConsumer#getExpectedMessageTypes()
   */
  @Override
  public Class[] getExpectedMessageTypes()
  {
    return new Class[]{SaldoMessage.class};
  }

  /**
   * @see de.willuhn.jameica.messaging.MessageConsumer#handleMessage(de.willuhn.jameica.messaging.Message)
   */
  @Override
  public void handleMessage(Message message) throws Exception
  {
    // Im Prinzip koennten wir auf eine ObjectChangedMessage hoeren. Beim Speichern eines Kontos in KontoControl
    // wurde bisher aber keine solche gesendet. Habe ich gerade erst eingefuegt. Da beim Speichern aber eine
    // SaldoMessage gesendet wird, nehmen wir erstmal die. Das hat den selben Effekt.
    final SaldoMessage m = (SaldoMessage) message;
    GenericObject ctx = m.getObject();
    if (!(ctx instanceof Konto))
      return;
    
    final Konto k = (Konto) ctx;
    final SupportStatus status = Plugin.getStatus(k);
    
    // Ist es ein Paypal-Konto?
    if (!status.checkInitial())
      return;
    
    // Ja, ist es. Wenn es unvollstaendig konfiguriert ist, fragen wir den User, ob es konfiguriert werden soll.
    if (status.checkAll())
      return;

    final String text = "Das Konto ist noch nicht vollst�ndig f�r die Nutzung mit Paypal konfiguriert.\n\n" +
                        "M�chten Sie den Assistenten zur Einrichtung des Kontos jetzt starten?";

    if (!Application.getCallback().askUser(i18n.tr(text)))
      return;

    new SetupPaypalStep1().handleAction(status);
  }

  /**
   * @see de.willuhn.jameica.messaging.MessageConsumer#autoRegister()
   */
  @Override
  public boolean autoRegister()
  {
    return true;
  }

}


