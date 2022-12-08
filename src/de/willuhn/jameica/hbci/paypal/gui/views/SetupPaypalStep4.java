/**********************************************************************
 *
 * Copyright (c) 2022 Olaf Willuhn
 * All rights reserved.
 * 
 * This software is copyrighted work licensed under the terms of the
 * Jameica License.  Please consult the file "LICENSE" for details. 
 *
 **********************************************************************/

package de.willuhn.jameica.hbci.paypal.gui.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ProgressBar;

import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.parts.Button;
import de.willuhn.jameica.gui.parts.InfoPanel;
import de.willuhn.jameica.gui.util.Container;
import de.willuhn.jameica.gui.util.SimpleContainer;
import de.willuhn.jameica.hbci.gui.action.KontoFetchUmsaetze;
import de.willuhn.jameica.hbci.gui.dialogs.SynchronizeOptionsDialog;
import de.willuhn.jameica.hbci.paypal.SupportStatus;
import de.willuhn.jameica.hbci.rmi.Konto;
import de.willuhn.jameica.messaging.StatusBarMessage;
import de.willuhn.jameica.system.Application;
import de.willuhn.jameica.system.OperationCanceledException;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;

/**
 * Finale View zum Einrichten eines Paypal-Kontos.
 */
public class SetupPaypalStep4 extends AbstractSetupPaypal
{
  private Konto konto      = null;

  private Button sync      = null;
  private Button configure = null;

  /**
   * @see de.willuhn.jameica.gui.AbstractView#bind()
   */
  @Override
  public void bind() throws Exception
  {
    GUI.getView().setTitle(i18n.tr("Paypal-Konto - Fertig!"));

    final SupportStatus status = (SupportStatus) this.getCurrentObject();
    this.konto = status.getKonto();

    final Container c = new SimpleContainer(this.getParent());
    
    final InfoPanel info = new InfoPanel()
    {
      /**
       * @see de.willuhn.jameica.gui.parts.InfoPanel#extend(de.willuhn.jameica.gui.parts.InfoPanel.DrawState, org.eclipse.swt.widgets.Composite, java.lang.Object)
       */
      @Override
      public Composite extend(DrawState state, Composite comp, Object context)
      {
        if (state == DrawState.TITLE_AFTER)
        {
          ProgressBar bar = createProgressBar(comp);
          bar.setSelection(100);
          return comp;
        }
        if (state == DrawState.COMMENT_BEFORE)
        {
          final Composite newComp = new Composite(comp,SWT.NONE);
          newComp.setBackground(comp.getBackground());
          newComp.setBackgroundMode(SWT.INHERIT_FORCE);
          newComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
          
          final GridLayout gl = new GridLayout(1,false);
          gl.marginWidth = 0;
          gl.horizontalSpacing = 0;
          newComp.setLayout(gl);

          final Composite comp2 = new Composite(newComp,SWT.NONE);
          comp2.setBackground(comp.getBackground());
          comp2.setBackgroundMode(SWT.INHERIT_FORCE);
          comp2.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));
          final GridLayout gl2 = new GridLayout(1,false);
          gl2.marginWidth = 0;
          gl2.horizontalSpacing = 0;
          comp2.setLayout(gl2);

          return newComp;
        }
        return super.extend(state, comp, context);
        
      }
    };
    info.setTitle(i18n.tr("Einrichtung abgeschlossen"));
    info.setIcon("paypal-large.png");
    info.setText(i18n.tr("Sie k�nnen nun den Saldo und die Ums�tze des Kontos abrufen oder die Synchonisierungsoptionen anpassen.\nEine ausf�hrliche Dokumentation finden Sie auch unter:"));
    info.setUrl("https://www.willuhn.de/wiki/doku.php?id=support:hibiscus.paypal");
    info.setComment(i18n.tr("Kontoinhaber: {0}\n\n" +
                            "Sie k�nnen diesen Assistenten sp�ter jederzeit erneut starten, indem Sie mit der rechten Maustaste\n" +
                            "auf das Konto klicken und im Kontextmen� die Option \"Paypal-Einrichtung starten...\" w�hlen.",konto.getName()));

    info.addButton(getSync());
    info.addButton(getConfigure());

    c.addPart(info);

    Application.getMessagingFactory().sendMessage(new StatusBarMessage(i18n.tr("Paypal-Konto erfolgreich eingerichtet"),StatusBarMessage.TYPE_SUCCESS));
  }
  
  /**
   * Liefert den Sync-Button.
   * @return der Sync-Button.
   */
  private Button getSync()
  {
    if (this.sync != null)
      return this.sync;

    this.sync = new Button(i18n.tr("Saldo und Ums�tze abrufen"), new KontoFetchUmsaetze(),konto,false,"mail-send-receive.png");
    return this.sync;
  }

  /**
   * Liefert den Button zum Konfigurieren der Synchronisierungsoptionen.
   * @return der Button zum Konfigurieren der Synchronisierungsoptionen.
   */
  private Button getConfigure()
  {
    if (this.configure != null)
      return this.configure;
    
    this.configure = new Button(i18n.tr("Synchronisierungsoptionen"),new Action() {
      public void handleAction(Object context) throws ApplicationException
      {
        try
        {
          SynchronizeOptionsDialog d = new SynchronizeOptionsDialog(konto,SynchronizeOptionsDialog.POSITION_CENTER);
          d.open();
        }
        catch (OperationCanceledException oce)
        {
          return;
        }
        catch (ApplicationException ae)
        {
          throw ae;
        }
        catch (Exception e)
        {
          Logger.error("unable to configure synchronize options",e);
          Application.getMessagingFactory().sendMessage(new StatusBarMessage(i18n.tr("Fehler beim Konfigurieren der Synchronisierungsoptionen: {0}",e.getMessage()),StatusBarMessage.TYPE_ERROR));
        }
        
      }
    },konto,false,"document-properties.png");
    return this.configure;
  }

}
