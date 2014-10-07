/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
/*
 * This code was generated by https://code.google.com/p/google-apis-client-generator/
 * (build: 2014-07-22 21:53:01 UTC)
 * on 2014-10-06 at 21:41:13 UTC 
 * Modify at your own risk.
 */

package tudelft.mdp.backend.endpoints.deviceLogEndpoint.model;

/**
 * Model definition for NfcLogRecord.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the deviceLogEndpoint. For a detailed explanation see:
 * <a href="http://code.google.com/p/google-http-java-client/wiki/JSON">http://code.google.com/p/google-http-java-client/wiki/JSON</a>
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class NfcLogRecord extends com.google.api.client.json.GenericJson {

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long id;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String nfcId;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Boolean state;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Double timestamp;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String user;

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getId() {
    return id;
  }

  /**
   * @param id id or {@code null} for none
   */
  public NfcLogRecord setId(java.lang.Long id) {
    this.id = id;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getNfcId() {
    return nfcId;
  }

  /**
   * @param nfcId nfcId or {@code null} for none
   */
  public NfcLogRecord setNfcId(java.lang.String nfcId) {
    this.nfcId = nfcId;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Boolean getState() {
    return state;
  }

  /**
   * @param state state or {@code null} for none
   */
  public NfcLogRecord setState(java.lang.Boolean state) {
    this.state = state;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Double getTimestamp() {
    return timestamp;
  }

  /**
   * @param timestamp timestamp or {@code null} for none
   */
  public NfcLogRecord setTimestamp(java.lang.Double timestamp) {
    this.timestamp = timestamp;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getUser() {
    return user;
  }

  /**
   * @param user user or {@code null} for none
   */
  public NfcLogRecord setUser(java.lang.String user) {
    this.user = user;
    return this;
  }

  @Override
  public NfcLogRecord set(String fieldName, Object value) {
    return (NfcLogRecord) super.set(fieldName, value);
  }

  @Override
  public NfcLogRecord clone() {
    return (NfcLogRecord) super.clone();
  }

}
