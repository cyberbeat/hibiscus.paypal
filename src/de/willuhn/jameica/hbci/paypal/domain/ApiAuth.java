/**********************************************************************
 *
 * Copyright (c) 2022 Olaf Willuhn
 * All rights reserved.
 * 
 * This software is copyrighted work licensed under the terms of the
 * Jameica License.  Please consult the file "LICENSE" for details. 
 *
 **********************************************************************/

package de.willuhn.jameica.hbci.paypal.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Das von der API zur�ckgelieferte Authentifizierungsobjekt.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class ApiAuth
{
  /**
   * Der zur�ckgelieferte Access-Token.
   */
  public String access_token = null;
  
  /**
   * Die Anzahl der Sekunden, nach denen die Authentifizierung abl�uft.
   */
  public long expires_in = -1;
  
  /**
   * Der Erstellzeitpunkt als Epochensekunden.
   */
  public long created = System.currentTimeMillis() / 1000;

}
