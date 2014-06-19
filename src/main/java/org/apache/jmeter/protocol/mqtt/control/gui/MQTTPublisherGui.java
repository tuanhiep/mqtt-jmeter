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
import org.apache.jmeter.gui.util.JLabeledRadioI18N;
import org.apache.jmeter.gui.util.JSyntaxTextArea;
import org.apache.jmeter.gui.util.JTextScrollPane;
import org.apache.jmeter.gui.util.VerticalPanel;
import org.apache.jmeter.protocol.mqtt.sampler.PublisherSampler;
import org.apache.jmeter.samplers.gui.AbstractSamplerGui;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.gui.JLabeledPasswordField;
import org.apache.jorphan.gui.JLabeledTextField;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

/**
 * @author Tuan Hiep
 * 
 */
public class MQTTPublisherGui extends AbstractSamplerGui implements
		ChangeListener {

	private static final long serialVersionUID = 240L;
	/** Take source from the named file */
	public static final String USE_FILE_RSC = "mqtt_use_file"; //$NON-NLS-1$
	/** Take source from a random file */
	public static final String USE_RANDOM_RSC = "mqtt_use_random_file"; //$NON-NLS-1$
	/** Take source from the text area */
	private static final String USE_TEXT_RSC = "mqtt_use_text"; //$NON-NLS-1$
	/** Create a TextMessage */
	public static final String TEXT_MSG_RSC = "mqtt_text_message"; //$NON-NLS-1$
	/** Create a big volume message */
	public static final String BIG_VOLUME = "mqtt_big_volume"; //$NON-NLS-1$
	/** Create a MapMessage */
	public static final String MAP_MSG_RSC = "mqtt_map_message"; //$NON-NLS-1$
	/** Create an ObjectMessage */
	public static final String OBJECT_MSG_RSC = "mqtt_object_message"; //$NON-NLS-1$
	/** Create a BytesMessage */
	public static final String BYTES_MSG_RSC = "mqtt_bytes_message"; //$NON-NLS-1$
	/** Create a message of type long number */
	public static final String LONG = "mqtt_long_value"; //$NON-NLS-1$
	/** Create a Message of type integer number */
	public static final String INT = "mqtt_int_value"; //$NON-NLS-1$
	/** Create a Message of type double number */
	public static final String DOUBLE = "mqtt_double_value"; //$NON-NLS-1$
	/** Create a Message of type double number */
	public static final String FLOAT = "mqtt_float_value"; //$NON-NLS-1$
	/** Create a Message of type String */
	public static final String STRING = "mqtt_string_value"; //$NON-NLS-1$

	// These are the names of properties used to define the labels
	private static final String DEST_SETUP_STATIC = "mqtt_dest_setup_static"; // $NON-NLS-1$
	private static final String DEST_SETUP_DYNAMIC = "mqtt_dest_setup_dynamic"; // $NON-NLS-1$
	public static final String GENERATED_VALUE = "mqtt_generated_value"; // $NON-NLS-1$
	public static final String FIXED_VALUE = "mqtt_fixed_value";// $NON-NLS-1$
	public static final String PSEUDO = "mqtt_pseudo_random";// $NON-NLS-1$
	public static final String SECURE = "mqtt_secure_random";// $NON-NLS-1$
	public static final String EXACTLY_ONCE = "mqtt_extactly_once";// $NON-NLS-1$
	public static final String AT_LEAST_ONCE = "mqtt_at_least_once";// $NON-NLS-1$
	public static final String AT_MOST_ONCE = "mqtt_at_most_once";// $NON-NLS-1$
	public static final String PLAIN_TEXT = "mqtt_plain_text";// $NON-NLS-1$
	public static final String BASE64 = "mqtt_base64";// $NON-NLS-1$
	public static final String BINHEX = "mqtt_binhex";// $NON-NLS-1$
	public static final String BINARY = "mqtt_binary";// $NON-NLS-1$
	public static final String NO_ENCODING = "mqtt_no_encoding";// $NON-NLS-1$
	public static final String ROUND_ROBIN = "mqtt_round_robin";// $NON-NLS-1$
	public static final String RANDOM = "mqtt_random";// $NON-NLS-1$
	// Button group resources
	private static final String[] DEST_SETUP_ITEMS = { DEST_SETUP_STATIC,DEST_SETUP_DYNAMIC };
	private final JLabeledRadioI18N destSetup = new JLabeledRadioI18N("mqtt_dest_setup", DEST_SETUP_ITEMS, DEST_SETUP_STATIC); // $NON-NLS-1$
	private static final String[] MSGTYPES_ITEMS = { TEXT_MSG_RSC,GENERATED_VALUE,FIXED_VALUE,BIG_VOLUME };
	private static final String[] TOPIC_CHOICES={ROUND_ROBIN,RANDOM};
	private static final String[] MSGFORMAT_ITEMS = {NO_ENCODING,BINARY,BASE64,BINHEX,PLAIN_TEXT};
	private static final String[] VALTYPES_ITEMS = { INT,LONG,FLOAT,DOUBLE};
	private static final String[] FVALTYPES_ITEMS = {INT,LONG,FLOAT,DOUBLE,STRING};
	private static final String[] RANTYPES_ITEMS = {PSEUDO,SECURE};
	private static final String[] QTYPES_ITEMS = {AT_MOST_ONCE,AT_LEAST_ONCE,EXACTLY_ONCE};
	private final JLabeledTextField urlField = new JLabeledTextField(JMeterUtils.getResString("mqtt_provider_url")); //$NON-NLS-1$
	private final JLabeledTextField mqttDestination = new JLabeledTextField(JMeterUtils.getResString("mqtt_topic")); //$NON-NLS-1$
	private final JCheckBox useAuth = new JCheckBox(JMeterUtils.getResString("mqtt_use_auth"), false); //$NON-NLS-1$
	private final JLabeledTextField mqttUser = new JLabeledTextField(JMeterUtils.getResString("mqtt_user")); //$NON-NLS-1$
	private final JLabeledTextField mqttPwd = new JLabeledPasswordField(JMeterUtils.getResString("mqtt_pwd")); //$NON-NLS-1$
	private final JLabeledTextField iterations = new JLabeledTextField(	JMeterUtils.getResString("mqtt_itertions")); //$NON-NLS-1$
	private final JSyntaxTextArea textMessage = new JSyntaxTextArea(10, 50); // $NON-NLS-1$
	private final JLabeledRadioI18N msgChoice = new JLabeledRadioI18N("mqtt_message_type", MSGTYPES_ITEMS, TEXT_MSG_RSC); //$NON-NLS-1$
	private final JLabeledRadioI18N msgFormat = new JLabeledRadioI18N("mqtt_message_format", MSGFORMAT_ITEMS,NO_ENCODING); //$NON-NLS-1$
	private final JLabeledRadioI18N topicChoice = new JLabeledRadioI18N("mqtt_topic_choice", TOPIC_CHOICES,ROUND_ROBIN); //$NON-NLS-1$
	private final JCheckBox connectionPerTopic = new JCheckBox(JMeterUtils.getResString("mqtt_connection_per_topic"), false); // $NON-NLS-1$
	private final JCheckBox suffixClientId = new JCheckBox(JMeterUtils.getResString("mqtt_suffix_client_id"),true); // $NON-NLS-1$
	private final JLabeledTextField suffixLength = new JLabeledTextField(JMeterUtils.getResString("mqtt_suffix_length")); //$NON-NLS-1$
	// For messages content
	private final JCheckBox useTimeStamp = new JCheckBox(JMeterUtils.getResString("mqtt_use_time_stamp"), false); // $NON-NLS-1$
	private final JCheckBox useNumberSeq = new JCheckBox(JMeterUtils.getResString("mqtt_use_number_seq"), false); // $NON-NLS-1$
	private final JCheckBox isRetained = new JCheckBox(JMeterUtils.getResString("mqtt_send_as_retained_msg"), false); // $NON-NLS-1$
	private final JLabeledRadioI18N typeQoSValue = new JLabeledRadioI18N("mqtt_qos", QTYPES_ITEMS,AT_MOST_ONCE); //$NON-NLS-1$
	private final JLabeledRadioI18N typeGeneratedValue = new JLabeledRadioI18N("mqtt_type_of_generated_value", VALTYPES_ITEMS,INT); //$NON-NLS-1$
	private final JLabeledRadioI18N typeFixedValue = new JLabeledRadioI18N("mqtt_type_of_fixed_value", FVALTYPES_ITEMS,INT); //$NON-NLS-1$
	private final JLabeledTextField min = new JLabeledTextField(JMeterUtils.getResString("mqtt_min_value")); //$NON-NLS-1$
	private final JLabeledTextField max = new JLabeledTextField(JMeterUtils.getResString("mqtt_max_value")); //$NON-NLS-1$
	private final JLabeledTextField value = new JLabeledTextField(JMeterUtils.getResString("mqtt_value")); //$NON-NLS-1$
	private final JLabeledRadioI18N typeRandom = new JLabeledRadioI18N("mqtt_type_random", RANTYPES_ITEMS,PSEUDO); //$NON-NLS-1$
	private final JLabeledTextField seed = new JLabeledTextField(JMeterUtils.getResString("mqtt_seed_random")); //$NON-NLS-1$
	private final JLabel textArea = new JLabel(JMeterUtils.getResString("mqtt_text_area"));
	private final JTextScrollPane textPanel = new JTextScrollPane(textMessage);
	private final JLabeledTextField clientId = new JLabeledTextField(JMeterUtils.getResString("mqtt_client_id")); //$NON-NLS-1$
	private  JComboBox<String> CharsetChooser =new JComboBox<String>(new String[] { "UTF-8", "UTF-16", "US-ASCII","UTF-16BE","UTF-16LE","ISO-8859-1"});
	private final JLabeledTextField sizeArray = new JLabeledTextField(JMeterUtils.getResString("mqtt_size_array")); //$NON-NLS-1$
	public MQTTPublisherGui() {
		init();
	}

	private void init() {
		setLayout(new BorderLayout());
		setBorder(makeBorder());
		add(makeTitlePanel(), BorderLayout.NORTH);
		JPanel mainPanel = new VerticalPanel();
		add(mainPanel, BorderLayout.CENTER);
//-----------------------------------URL/CLIENT_ID---------------------------------------//
		JPanel DPanel = new JPanel();
		DPanel.setLayout(new BoxLayout(DPanel, BoxLayout.X_AXIS));
		DPanel.add(urlField);
		DPanel.add(clientId);
		DPanel.add(suffixClientId);
		DPanel.add(suffixLength);
		JPanel ControlPanel = new VerticalPanel();
		ControlPanel.add(DPanel);
		ControlPanel.add(createDestinationPane());
		ControlPanel.add(createAuthPane());
		ControlPanel.add(iterations);
		ControlPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.gray),"Connection Info"));
		mainPanel.add(ControlPanel);		
//---------------------------------------Message Format----------------------------------//
		JPanel FormatPanel = new JPanel();
		FormatPanel.setLayout(new BoxLayout(FormatPanel, BoxLayout.X_AXIS));
		msgFormat.setLayout(new BoxLayout(msgFormat, BoxLayout.X_AXIS));
		FormatPanel.add(msgFormat);
		Dimension minSize = new Dimension(10, 15);
		Dimension prefSize = new Dimension(10, 15);
		Dimension maxSize = new Dimension(Short.MAX_VALUE, 100);
		FormatPanel.add(new Box.Filler(minSize, prefSize, maxSize));
		FormatPanel.add(CharsetChooser);
		JPanel EncodePanel = new VerticalPanel();
		EncodePanel.add(FormatPanel);
		EncodePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.gray),"Encoding"));
		mainPanel.add(EncodePanel);		
		JPanel StampPanel = new VerticalPanel();
		StampPanel.add(useTimeStamp);
		StampPanel.add(useNumberSeq);
		StampPanel.add(isRetained);
		typeQoSValue.setLayout(new BoxLayout(typeQoSValue, BoxLayout.X_AXIS));
		StampPanel.add(this.typeQoSValue);	
		StampPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.gray),"Option"));
		mainPanel.add(StampPanel);		
//--------------------------------------Message Type-------------------------------------//		
		JPanel ContentPanel = new VerticalPanel();		
		msgChoice.setLayout(new BoxLayout(msgChoice, BoxLayout.X_AXIS));
		ContentPanel.add(msgChoice);
		ContentPanel.add(sizeArray);

//----------------------------------Fixed Value Panel------------------------------------//	
		JPanel FPanel = new JPanel();
		typeFixedValue.setLayout(new BoxLayout(typeFixedValue, BoxLayout.Y_AXIS));
		FPanel.add(typeFixedValue);
		FPanel.add(value);
		ContentPanel.add(FPanel);		
//----------------------------------Generated Value Panel--------------------------------//		
		JPanel GPanel = new JPanel();
		typeGeneratedValue.setLayout(new BoxLayout(typeGeneratedValue, BoxLayout.Y_AXIS));
		GPanel.add(typeGeneratedValue);
		GPanel.add(min);		
		GPanel.add(max);
		this.typeRandom.setLayout(new BoxLayout(typeRandom, BoxLayout.Y_AXIS));
		GPanel.add(typeRandom);
		GPanel.add(seed);
		ContentPanel.add(GPanel);
//---------------------------------Big Volume ------------------------------------------//
		
		ContentPanel.add(sizeArray);
//-------------------------------------Content Panel -----------------------------------//		
		 
		JPanel messageContentPanel = new JPanel(new BorderLayout());
		messageContentPanel.add(this.textArea,	BorderLayout.NORTH);
		messageContentPanel.add(this.textPanel,BorderLayout.CENTER);
		ContentPanel.add(messageContentPanel);
		ContentPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.gray),"Content"));
		mainPanel.add(ContentPanel);
		useAuth.addChangeListener(this);
        msgChoice.addChangeListener(this);
		typeFixedValue.addChangeListener(this);
		typeQoSValue.addChangeListener(this);
		typeRandom.addChangeListener(this);
	 	msgFormat.addChangeListener(this);
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
	 * 
	 * @return JPanel that contains destination infos
	 */
	private Component createDestinationPane() {
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

	/**
	 * To Clear the GUI
	 */
	@Override
	public void clearGui() {
		super.clearGui();
		urlField.setText(""); // $NON-NLS-1$
		mqttDestination.setText(""); // $NON-NLS-1$
		mqttUser.setText(""); // $NON-NLS-1$
		mqttPwd.setText(""); // $NON-NLS-1$
		textMessage.setInitialText(""); // $NON-NLS-1$
		msgChoice.setText(""); // $NON-NLS-1$
		updateConfig(USE_TEXT_RSC);
		msgChoice.setText(TEXT_MSG_RSC);
		iterations.setText("1"); // $NON-NLS-1$
		useAuth.setSelected(false);
		mqttUser.setEnabled(false);
		mqttPwd.setEnabled(false);
		destSetup.setText(DEST_SETUP_STATIC);
		textArea.setText("");
	    clientId.setText("");
	    connectionPerTopic.setSelected(false);
	   
		
	}

	private void setupSamplerProperties(PublisherSampler sampler) {
		this.configureTestElement(sampler);
		sampler.setProviderUrl(urlField.getText());
		sampler.setDestination(mqttDestination.getText());
		sampler.setUsername(mqttUser.getText());
		sampler.setPassword(mqttPwd.getText());
		sampler.setTextMessage(textMessage.getText());
		sampler.setMessageChoice(msgChoice.getText());
		sampler.setIterations(iterations.getText());
		sampler.setUseAuth(useAuth.isSelected());
		sampler.setQuality(typeQoSValue.getText());
        sampler.setRetained(isRetained.isSelected());
        sampler.setTYPE_FIXED_VALUE(typeFixedValue.getText());
        sampler.setFIXED_VALUE(this.value.getText());
        sampler.setTYPE_RANDOM_VALUE(this.typeRandom.getText());
        sampler.setMAX_RANDOM_VALUE(this.max.getText());
        sampler.setMIN_RANDOM_VALUE(this.min.getText());
        sampler.setTYPE_GENERATED_VALUE(this.typeGeneratedValue.getText());
        sampler.setSEED(this.seed.getText());
        sampler.setUSE_TIMESTAMP(useTimeStamp.isSelected());
        sampler.setUSE_NUMBER_SEQUENCE(useNumberSeq.isSelected());
        sampler.setCLIENT_ID(clientId.getText());
        sampler.setFORMAT(msgFormat.getText());
        sampler.setCHARSET((String) this.CharsetChooser.getSelectedItem());
        sampler.setSIZE_ARRAY(this.sizeArray.getText());
        sampler.setSTRATEGY(this.topicChoice.getText());
        sampler.setOneConnectionPerTopic(this.connectionPerTopic.isSelected());
        sampler.setRandomSuffix(this.suffixClientId.isSelected());
        sampler.setLength(this.suffixLength.getText());
	}
		
	/**
	 * the implementation loads the URL and the soap action for the request.
	 */
	@Override
	public void configure(TestElement el) {
		super.configure(el);
		PublisherSampler sampler = (PublisherSampler) el;
		urlField.setText(sampler.getProviderUrl());
		mqttDestination.setText(sampler.getDestination());
		mqttUser.setText(sampler.getUsername());
		mqttPwd.setText(sampler.getPassword());
		textMessage.setInitialText(sampler.getTextMessage());
		textMessage.setCaretPosition(0);
        clientId.setText(sampler.getCLIENT_ID());
        connectionPerTopic.setSelected(sampler.isOneConnectionPerTopic());
        msgChoice.setText(sampler.getMessageChoice());
        iterations.setText(sampler.getIterations());
        useAuth.setSelected(sampler.isUseAuth());
        mqttUser.setEnabled(useAuth.isSelected());
        mqttPwd.setEnabled(useAuth.isSelected());
        updateChoice(msgChoice.getText());
        updateChoice(msgFormat.getText());
        updateChoice("Suffix=" + String.valueOf(this.suffixClientId.isSelected()));
	
	}
	
	@Override
	public TestElement createTestElement() {
		PublisherSampler sampler = new PublisherSampler();
		setupSamplerProperties(sampler);
		return sampler;
	}
	/**
	 * To Update the parameter of field in the GUI
	 * 
	 * @param command
	 */

	private void updateConfig(String command) {

		if (command.equals(USE_TEXT_RSC)) {
			textMessage.setEnabled(true);

		} 
	
	}

	@Override
	public String getLabelResource() {
		return "mqtt_publisher"; //$NON-NLS-1$
	}

	@Override
	public void modifyTestElement(TestElement s) {
		PublisherSampler sampler = (PublisherSampler) s;
		setupSamplerProperties(sampler);
	}

	/**
	 * When we change some parameter by clicking on the GUI
	 */
	@Override
	public void stateChanged(ChangeEvent event) {

	
		if (event.getSource() == msgChoice) {
			updateChoice(msgChoice.getText());
		} else if (event.getSource() == useAuth) {
			mqttUser.setEnabled(useAuth.isSelected());
			mqttPwd.setEnabled(useAuth.isSelected());
		} 
		else if (event.getSource()==msgFormat){
			updateChoice(msgFormat.getText());
		} 
		else if(event.getSource()==suffixClientId){
			updateChoice("Suffix="+String.valueOf(this.suffixClientId.isSelected()));
		}
	}

	/**
	 * To Update the choice of message to send
	 * 
	 * @param command
	 */
	private void updateChoice(String command) {
		
		if(TEXT_MSG_RSC.equals(command)){			
			this.typeGeneratedValue.setVisible(false);
			this.typeFixedValue.setVisible(false);
			this.max.setVisible(false);
			this.min.setVisible(false);
			this.value.setVisible(false);
			this.typeRandom.setVisible(false);
			this.seed.setVisible(false);
			this.textArea.setVisible(true);
			this.textPanel.setVisible(true);
			this.sizeArray.setVisible(false);													}
		else if(GENERATED_VALUE.equals(command)) {
			this.typeFixedValue.setVisible(false);
			this.value.setVisible(false);
			this.textArea.setVisible(false);
			this.textPanel.setVisible(false);
			this.typeGeneratedValue.setVisible(true);
			this.max.setVisible(true);
			this.min.setVisible(true);
			this.typeRandom.setVisible(true);
			this.seed.setVisible(true);
			this.sizeArray.setVisible(false);
		} else if(FIXED_VALUE.equals(command)){
			this.typeGeneratedValue.setVisible(false);
			this.typeFixedValue.setVisible(true);
			this.max.setVisible(false);
			this.min.setVisible(false);
			this.value.setVisible(true);
			this.typeRandom.setVisible(false);
			this.seed.setVisible(false);
			this.textArea.setVisible(false);
			this.textPanel.setVisible(false);
			this.sizeArray.setVisible(false);								}
		else if(BIG_VOLUME.equals(command)){			
			this.typeGeneratedValue.setVisible(false);
			this.typeFixedValue.setVisible(false);
			this.max.setVisible(false);
			this.min.setVisible(false);
			this.value.setVisible(false);
			this.typeRandom.setVisible(false);
			this.seed.setVisible(false);
			this.textArea.setVisible(false);
			this.textPanel.setVisible(false);
			this.sizeArray.setVisible(true);
		}
		else if(BINARY.equals(command)){
			this.CharsetChooser.setVisible(false);
		}
		else if(BINHEX.equals(command)){
			this.CharsetChooser.setVisible(false);
		}
		else if(BASE64.equals(command)){
			this.CharsetChooser.setVisible(false);
		}
		else if(PLAIN_TEXT.equals(command)){			
			this.CharsetChooser.setVisible(true);			
		}
		else if(NO_ENCODING.equals(command)){
			this.CharsetChooser.setVisible(false);
		}
		else if("suffix=true".equalsIgnoreCase(command)){
			this.suffixLength.setVisible(true);
			
		}
		else if("suffix=false".equalsIgnoreCase(command)){
			this.suffixLength.setVisible(false);
		}
		validate();
	}

}
 