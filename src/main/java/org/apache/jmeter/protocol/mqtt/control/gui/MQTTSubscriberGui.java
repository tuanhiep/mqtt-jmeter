/**
 Licensed to the Apache Software Foundation (ASF) under one
 or more contributor license agreements.  See the NOTICE file
 distributed with this work for additional information
 regarding copyright ownership.  The ASF licenses this file
 to you under the Apache License, Version 2.0 (the
 "License"); you may not use this file except in compliance
 with the License.  You may obtain a copy of the License at
 
    http://www.apache.org/licenses/LICENSE-2.0
 
 Unless required by applicable law or agreed to in writing,
 software distributed under the License is distributed on an
 "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 KIND, either express or implied.  See the License for the
 specific language governing permissions and limitations
 under the License. 

  Copyright 2014 University Joseph Fourier, LIG Laboratory, ERODS Team

*/

package org.apache.jmeter.protocol.mqtt.control.gui;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.jmeter.gui.util.JLabeledRadioI18N;
import org.apache.jmeter.gui.util.VerticalPanel;
import org.apache.jmeter.protocol.mqtt.sampler.SubscriberSampler;
import org.apache.jmeter.samplers.gui.AbstractSamplerGui;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.gui.JLabeledPasswordField;
import org.apache.jorphan.gui.JLabeledTextField;

/**
 * This is the GUI for mqtt Subscriber <br>
 *
 */
public class MQTTSubscriberGui extends AbstractSamplerGui implements ChangeListener {

    private static final long serialVersionUID = 240L;
    public static final String AT_MOST_ONCE = "mqtt_at_most_once";// $NON-NLS-1$
    public static final String EXACTLY_ONCE = "mqtt_extactly_once";// $NON-NLS-1$
	public static final String AT_LEAST_ONCE = "mqtt_at_least_once";// $NON-NLS-1$
    private static final String[] QTYPES_ITEMS = {AT_MOST_ONCE,AT_LEAST_ONCE,EXACTLY_ONCE};
    public static final String ROUND_ROBIN = "mqtt_round_robin";// $NON-NLS-1$
    public static final String RANDOM = "mqtt_random";// $NON-NLS-1$
    private static final String[] TOPIC_CHOICES={ROUND_ROBIN,RANDOM};
    private final JLabeledTextField urlField = new JLabeledTextField(JMeterUtils.getResString("mqtt_provider_url")); // $NON-NLS-1$
    private final JLabeledTextField mqttDestination = new JLabeledTextField(JMeterUtils.getResString("mqtt_topic")); // $NON-NLS-1$
    private final JLabeledTextField mqttUser = new JLabeledTextField(JMeterUtils.getResString("mqtt_user")); // $NON-NLS-1$
    private final JLabeledTextField mqttPwd = new JLabeledPasswordField(JMeterUtils.getResString("mqtt_pwd")); // $NON-NLS-1$
    private final JCheckBox useAuth = new JCheckBox(JMeterUtils.getResString("mqtt_use_auth"), false); //$NON-NLS-1$
    private final JLabeledTextField timeout =  new JLabeledTextField(JMeterUtils.getResString("mqtt_timeout")); //$NON-NLS-1$
    private final JLabeledTextField separator =  new JLabeledTextField(JMeterUtils.getResString("mqtt_separator")); //$NON-NLS-1$
    private final JCheckBox suffixClientId = new JCheckBox(JMeterUtils.getResString("mqtt_suffix_client_id"),true); // $NON-NLS-1$
    private final JLabeledTextField suffixLength = new JLabeledTextField(JMeterUtils.getResString("mqtt_suffix_length")); //$NON-NLS-1$
    private final JCheckBox connectionPerTopic = new JCheckBox(JMeterUtils.getResString("mqtt_connection_per_topic"), false); // $NON-NLS-1$
    private final JLabeledRadioI18N topicChoice = new JLabeledRadioI18N("mqtt_topic_choice", TOPIC_CHOICES,ROUND_ROBIN); //$NON-NLS-1$
    private final JCheckBox stopBetweenSamples = new JCheckBox(JMeterUtils.getResString("mqtt_stop_between_samples"), true); // $NON-NLS-1$
    private final JLabeledTextField clientId = new JLabeledTextField(JMeterUtils.getResString("mqtt_client_id")); //$NON-NLS-1$
    private final JLabeledRadioI18N typeQoSValue = new JLabeledRadioI18N("mqtt_qos", QTYPES_ITEMS,AT_MOST_ONCE); //$NON-NLS-1$
    private final JCheckBox cleanSession = new JCheckBox(JMeterUtils.getResString("mqtt_clean_session"), false); // $NON-NLS-1$
    
    private final JLabeledTextField keepAlive = new JLabeledTextField(JMeterUtils.getResString("mqtt_keep_alive")); // $NON-NLS-1$
    public MQTTSubscriberGui() {
        init();
    }

    @Override
    public String getLabelResource() {
        return "mqtt_subscriber_title"; // $NON-NLS-1$
    }

    /**
     * @see org.apache.jmeter.gui.JMeterGUIComponent#createTestElement()
     */
    @Override
    public TestElement createTestElement() {
        SubscriberSampler sampler = new SubscriberSampler();
        modifyTestElement(sampler);
        return sampler;
    }

    /**
     * Modifies a given TestElement to mirror the data in the gui components.
     *
     * @see org.apache.jmeter.gui.JMeterGUIComponent#modifyTestElement(TestElement)
     */
    @Override
    public void modifyTestElement(TestElement s) {
        SubscriberSampler sampler = (SubscriberSampler) s;
        this.configureTestElement(sampler);
        sampler.setProviderUrl(urlField.getText());
        sampler.setDestination(mqttDestination.getText());
        sampler.setClientID(clientId.getText());
        sampler.setUsername(mqttUser.getText());
        sampler.setPassword(mqttPwd.getText());
        sampler.setUseAuth(useAuth.isSelected());
        sampler.setKeepAlive(keepAlive.getText());
        sampler.setTimeout(timeout.getText());
        sampler.setRandomSuffix(this.suffixClientId.isSelected());
        sampler.setLength(this.suffixLength.getText());
        sampler.setOneConnectionPerTopic(this.connectionPerTopic.isSelected());
        sampler.setSTRATEGY(this.topicChoice.getText());
        sampler.setQuality(typeQoSValue.getText());
        sampler.setCLEANSESSION(cleanSession.isSelected());
        
    }

    private void init() {
        setLayout(new BorderLayout());
        setBorder(makeBorder());
        add(makeTitlePanel(), BorderLayout.NORTH);
        JPanel mainPanel = new VerticalPanel();
        add(mainPanel, BorderLayout.CENTER);              
        JPanel DPanel = new JPanel();
		DPanel.setLayout(new BoxLayout(DPanel, BoxLayout.X_AXIS));
		DPanel.add(urlField);
		DPanel.add(clientId);	
		DPanel.add(suffixClientId);
		DPanel.add(suffixLength);
		JPanel ControlPanel = new VerticalPanel();
		ControlPanel.add(DPanel);
		ControlPanel.add(createDestinationPane());
		ControlPanel.add(cleanSession);
		ControlPanel.add(createAuthPane());
		ControlPanel.add(keepAlive);
		ControlPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.gray),"Connection Info"));
		mainPanel.add(ControlPanel);	
		JPanel TPanel = new VerticalPanel();
		TPanel.setLayout(new BoxLayout(TPanel, BoxLayout.X_AXIS));
		timeout.setLayout(new BoxLayout(timeout, BoxLayout.X_AXIS));
		typeQoSValue.setLayout(new BoxLayout(typeQoSValue, BoxLayout.X_AXIS));
		TPanel.add(typeQoSValue);
		TPanel.add(timeout);
		TPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.gray),"Option"));
		mainPanel.add(TPanel);
		useAuth.addChangeListener(this);
		suffixClientId.addChangeListener(this);
		
    }
    /**
	 * 
	 * @return JPanel Panel with checkbox to choose  user and password
	 */
	private Component createAuthPane() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		panel.add(useAuth);
		panel.add(Box.createHorizontalStrut(10));
		panel.add(mqttUser);
		panel.add(Box.createHorizontalStrut(10));
		panel.add(mqttPwd);
		return panel;
	}
    /**
     * the implementation loads the URL and the soap action for the request.
     */
    @Override
    public void configure(TestElement el) {
        super.configure(el);
        SubscriberSampler sampler = (SubscriberSampler) el;  
        urlField.setText(sampler.getProviderUrl());        
        mqttDestination.setText(sampler.getDestination());
        clientId.setText(sampler.getClientId());  
        suffixLength.setText(sampler.getLength());
        mqttUser.setText(sampler.getUsername());
        mqttPwd.setText(sampler.getPassword());
        keepAlive.setText(String.valueOf(sampler.getKeepAlive()));
        useAuth.setSelected(sampler.isUseAuth());
        mqttUser.setEnabled(useAuth.isSelected());
        mqttPwd.setEnabled(useAuth.isSelected());
        timeout.setText(sampler.getTimeout());       
    }

    @Override
    public void clearGui(){
        super.clearGui();
        urlField.setText(""); // $NON-NLS-1$
        mqttDestination.setText(""); // $NON-NLS-1$
        clientId.setText(""); // $NON-NLS-1$     
        mqttUser.setText(""); // $NON-NLS-1$
        mqttPwd.setText(""); // $NON-NLS-1$
        timeout.setText(""); // $NON-NLS-1$
        separator.setText(""); // $NON-NLS-1$
        keepAlive.setText("60");
        useAuth.setSelected(false);
        mqttUser.setEnabled(false);
        mqttPwd.setEnabled(false);
        stopBetweenSamples.setSelected(false);
      
    }

    /**
     * When the state of a widget changes, it will notify the gui. the method
     * then enables or disables certain parameters.
     */
    @Override
    public void stateChanged(ChangeEvent event) {
        if (event.getSource() == useAuth) {
            mqttUser.setEnabled(useAuth.isSelected());
            mqttPwd.setEnabled(useAuth.isSelected());
        }
        else if(event.getSource()==suffixClientId){
			updateChoice("Suffix="+String.valueOf(this.suffixClientId.isSelected()));
		}
    }
    
    private void updateChoice(String command) {
    	if("suffix=true".equalsIgnoreCase(command)){
			this.suffixLength.setVisible(true);			
		}
		else if("suffix=false".equalsIgnoreCase(command)){
			this.suffixLength.setVisible(false);
		}
		validate();		
	}

	private JPanel createDestinationPane() {
        JPanel panel = new VerticalPanel(); //new BorderLayout(3, 0)
		this.mqttDestination.setLayout((new BoxLayout(mqttDestination, BoxLayout.X_AXIS)));
		panel.add(mqttDestination);
		JPanel TPanel = new JPanel();
		TPanel.setLayout(new BoxLayout(TPanel,BoxLayout.X_AXIS));		
		this.connectionPerTopic.setLayout(new BoxLayout(connectionPerTopic,BoxLayout.X_AXIS));
		this.connectionPerTopic.setAlignmentX(CENTER_ALIGNMENT);
		TPanel.add(connectionPerTopic);
		TPanel.add(Box.createHorizontalStrut(100));
		this.topicChoice.setLayout(new BoxLayout(topicChoice,BoxLayout.X_AXIS));
		TPanel.add(topicChoice);
		panel.add(TPanel);
		return panel;
     }
}