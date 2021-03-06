/*
 * Copyright 2014 Sam Elsharif
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package info.msxlaunchers.openmsx.launcher.ui.view.swing;

import info.msxlaunchers.openmsx.common.FileTypeUtils;
import info.msxlaunchers.openmsx.common.Utils;
import info.msxlaunchers.openmsx.launcher.data.game.Game;
import info.msxlaunchers.openmsx.launcher.data.game.constants.Genre;
import info.msxlaunchers.openmsx.launcher.data.game.constants.MSXGeneration;
import info.msxlaunchers.openmsx.launcher.data.game.constants.Medium;
import info.msxlaunchers.openmsx.launcher.data.game.constants.Sound;
import info.msxlaunchers.openmsx.launcher.data.repository.RepositoryGame;
import info.msxlaunchers.openmsx.launcher.data.settings.constants.Language;
import info.msxlaunchers.openmsx.launcher.ui.presenter.GamePropertiesPresenter;
import info.msxlaunchers.openmsx.launcher.ui.view.swing.component.HyperLink;
import info.msxlaunchers.openmsx.launcher.ui.view.swing.component.JTextFieldBorderless;
import info.msxlaunchers.openmsx.launcher.ui.view.swing.images.Icons;
import info.msxlaunchers.openmsx.launcher.ui.view.swing.language.LanguageDisplayFactory;

import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

/**
 * Game Properties dialog class
 * 
 * @since v1.2
 * @author Sam Elsharif
 *
 */
@SuppressWarnings("serial")
public class GamePropertiesWindow extends JDialog implements ActionListener
{
	private final GamePropertiesPresenter presenter;
	private final Game game;
	private final RepositoryGame repositoryGame;
	private final int knownDumps;
	private final List<String> fileGroup;
	private final Map<String,String> messages;
	private final boolean rightToLeft;
	private final Component mainWindow;

	private JPanel tablePane = null;
	private GridBagLayout tableLayout = null;
	private GridBagConstraints labelConstraints = null;
	private GridBagConstraints valueConstraints = null;

	private JButton okButton;

	private static final String SEPARATOR = ", ";

	private static final Map<String,String> countryFlag = new HashMap<>();
	static
	{
		countryFlag.put("BR", "FLAG_pt_BR");
		countryFlag.put("DE", "FLAG_de_DE");
		countryFlag.put("ES", "FLAG_es_ES");
		countryFlag.put("FR", "FLAG_fr_FR");
		countryFlag.put("GB", "FLAG_UK");
		countryFlag.put("HK", "FLAG_HK");
		countryFlag.put("IT", "FLAG_it_IT");
		countryFlag.put("JP", "FLAG_ja_JP");
		countryFlag.put("KR", "FLAG_ko_KR");
		countryFlag.put("KW", "FLAG_KW");
		countryFlag.put("NL", "FLAG_nl_NL");
		countryFlag.put("PT", "FLAG_PT");
		countryFlag.put("RU", "FLAG_ru_RU");
		countryFlag.put("SA", "FLAG_SA");
		countryFlag.put("SE", "FLAG_sv_SE");
		countryFlag.put("UK", "FLAG_UK");
		countryFlag.put("US", "FLAG_en_US");
		countryFlag.put("TW", "FLAG_zh_TW");
		countryFlag.put("CA", "FLAG_CA");
		countryFlag.put("EU", "FLAG_EU");
	}

	private static final Map<String,String> generationImage = new HashMap<>();
	static
	{
		generationImage.put(MSXGeneration.MSX.getDisplayName(), "GENERATION_MSX_LARGE");
		generationImage.put(MSXGeneration.MSX2.getDisplayName(), "GENERATION_MSX2_LARGE");
		generationImage.put(MSXGeneration.MSX2Plus.getDisplayName(), "GENERATION_MSX2P_LARGE");
		generationImage.put(MSXGeneration.TURBO_R.getDisplayName(), "GENERATION_TURBO_R_LARGE");
	}

	public GamePropertiesWindow(GamePropertiesPresenter presenter,
								Game game,
								RepositoryGame repositoryGame,
								int knownDumps,
								List<String> fileGroup,
								Language language,
								boolean rightToLeft)
	{
		this.presenter = presenter;
		this.game = game;
		this.fileGroup = fileGroup;
		this.repositoryGame = repositoryGame;
		this.knownDumps = knownDumps;
		this.rightToLeft = rightToLeft;
		this.messages = LanguageDisplayFactory.getDisplayMessages(getClass(), language);
		this.mainWindow = GlobalSwingContext.getIntance().getMainWindow();
	}

	public void display()
	{
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setTitle(messages.get("PROPERTIES"));
		setModalityType(ModalityType.APPLICATION_MODAL);
		setResizable(false);

		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);

		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));

		tablePane = new JPanel();
		if(rightToLeft)
		{
			tablePane.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		}
		tableLayout = new GridBagLayout();
		tablePane.setLayout(tableLayout);
		labelConstraints = new GridBagConstraints();
		valueConstraints = new GridBagConstraints();

		valueConstraints.fill = GridBagConstraints.HORIZONTAL;
		valueConstraints.anchor = GridBagConstraints.NORTHWEST;
		valueConstraints.weightx = 1.0;
		valueConstraints.gridwidth = GridBagConstraints.REMAINDER;
		valueConstraints.insets = new Insets(2, 2, 2, 4);

        labelConstraints = (GridBagConstraints)valueConstraints.clone();
        labelConstraints.weightx = 0.0;
        labelConstraints.gridwidth = 1;

        addPropertyToDisplay(messages.get("NAME"), game.getName());
        if(repositoryGame != null)
        {
            addPropertyToDisplay(messages.get("COMMON_NAME"), repositoryGame.getTitle());
        }
        File mainFile = new File(FileTypeUtils.getMainFile(game.getRomA(), game.getRomB(), game.getDiskA(), game.getDiskB(),
        		game.getTape(), game.getHarddisk(), game.getLaserdisc(), game.getTclScript()));
        addPropertyToDisplay(messages.get("FILE"), mainFile.getAbsolutePath());
        addPropertyToDisplay(messages.get("MEDIUM"), getMedium());

        if(repositoryGame != null)
        {
            addPropertyToDisplay(messages.get("SYSTEM"), repositoryGame.getSystem());
            addPropertyToDisplay(messages.get("KNOWN_DUMPS"), String.valueOf(knownDumps));
        }
        if(game.getSize() > 0)
        {
        	addPropertyToDisplay(messages.get("SIZE"), Utils.getString(game.getSize() / 1024) + " KB");
        }
        addPropertyToDisplay("SHA1", game.getSha1Code(), true);
        if(repositoryGame != null)
        {
            addPropertyToDisplay(messages.get("COMPANY"), repositoryGame.getCompany());
            addPropertyToDisplay(messages.get("YEAR"), repositoryGame.getYear());
            addPropertyToDisplayWithIcon(messages.get("COUNTRY"), repositoryGame.getCountry(), countryFlag);
            if(game.isROM())
            {
            	addPropertyToDisplay(messages.get("MAPPER"), repositoryGame.getMapper());
            }
            addPropertyToDisplay(messages.get("START"), repositoryGame.getStart());
            addPropertyToDisplay(messages.get("DUMP"), repositoryGame.getOriginalText());
            addPropertyToDisplay(messages.get("REMARK"), repositoryGame.getRemark());
        }
        addPropertyToDisplayAsIcon(messages.get("GENERATION"), getGenerationList(game), generationImage);

        addPropertyToDisplay(messages.get("SOUND"), getSound(game));
        addPropertyToDisplay(messages.get("GENRE"), getGenre(game));
        if(presenter.isGenerationMSXIdValid(game))
        {
            addLinkToDisplay("Generation-MSX ID", Utils.getString(game.getMsxGenID()), presenter.getGenerationMSXURL(game), true);
        }

		JPanel buttonsPane = new JPanel();
		buttonsPane.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		okButton = new JButton(messages.get("OK"));
		okButton.addActionListener(this);
		okButton.setPreferredSize(MainWindow.BUTTON_DIMENSION);
		buttonsPane.add(okButton);

		contentPane.add(tablePane);
		contentPane.add(buttonsPane);

		pack();
		setLocationRelativeTo(mainWindow);
		setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == okButton)
		{
			dispose();
		}
	}

	private void addPropertyToDisplayWithIcon(String attribute, String value, Map<String,String> valueToIcon)
	{
		if(!Utils.isEmpty(value))
		{
			String localizedValue = messages.get(value);
			if(!Utils.isEmpty(localizedValue))
			{
				addAttribute(attribute, false);

				JLabel valueLabel = new JLabel();
				valueLabel.setIcon(Icons.valueOf(valueToIcon.get(value)).getImageIcon());
				valueLabel.setText(localizedValue);
				tableLayout.setConstraints(valueLabel, valueConstraints);
				tablePane.add(valueLabel);

				if(rightToLeft)
				{
					valueLabel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
				}
			}
		}
	}

	private void addPropertyToDisplayAsIcon(String attribute, List<String> values, Map<String,String> valueToIcon)
	{
		if(!values.isEmpty())
		{
			addAttribute(attribute, false);

			JPanel valuePanel = new JPanel(new FlowLayout(FlowLayout.LEADING, 0, 1));

			for(String value: values)
			{
				JLabel valueLabel = new JLabel();
				valueLabel.setIcon(Icons.valueOf(valueToIcon.get(value)).getImageIcon());
				valuePanel.add(valueLabel);
				valuePanel.add(Box.createHorizontalStrut(8));
			}

			tableLayout.setConstraints(valuePanel, valueConstraints);
			tablePane.add(valuePanel);

			if(rightToLeft)
			{
				valuePanel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
			}
		}
	}

	private void addPropertyToDisplay(String attribute, String value)
	{
		addPropertyToDisplay(attribute, value, false);
	}

	private void addPropertyToDisplay(String attribute, String value, boolean colonOnTheLeft)
	{
		if(!Utils.isEmpty(value))
		{
			addAttribute(attribute, colonOnTheLeft);

			JTextField valueTextField = new JTextFieldBorderless(value);

			if(rightToLeft)
			{
				valueTextField.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
			}

			tableLayout.setConstraints(valueTextField, valueConstraints);
			tablePane.add(valueTextField);
		}
	}

	private void addAttribute(String attribute, boolean colonOnTheLeft)
	{
		JLabel attributeLabel;
		if(rightToLeft && colonOnTheLeft)
		{
			attributeLabel = new JLabel(":" + attribute);
		}
		else
		{
			attributeLabel = new JLabel(attribute + ":");
		}
		tableLayout.setConstraints(attributeLabel, labelConstraints);
		tablePane.add(attributeLabel);

		if(rightToLeft)
		{
			attributeLabel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		}
	}

	private void addLinkToDisplay(String attribute, String label, String address, boolean colonOnTheLeft)
	{
		JLabel attributeLabel;
		if(rightToLeft && colonOnTheLeft)
		{
			attributeLabel = new JLabel(":" + attribute);
		}
		else
		{
			attributeLabel = new JLabel(attribute + ":");				
		}
        tableLayout.setConstraints(attributeLabel, labelConstraints);
        tablePane.add(attributeLabel);

        JPanel linkPanel = new JPanel();
        linkPanel.setLayout(new FlowLayout(FlowLayout.LEADING, 0, 0));
        linkPanel.add(HyperLink.label(label).address(address).build());
        tableLayout.setConstraints(linkPanel, valueConstraints);
        tablePane.add(linkPanel);

        if(rightToLeft)
        {
        	attributeLabel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        	linkPanel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        }
	}

	private String getMedium(Game game)
	{
		String medium = null;

		if(game.isROM())
		{
			medium = Medium.ROM.toString();
		}
		else if(game.isDisk())
		{
			medium = Medium.DISK.toString();
		}
		else if(game.isTape())
		{
			medium = Medium.TAPE.toString();
		}
		else if(game.isHarddisk())
		{
			medium = Medium.HARDDISK.toString();
		}
		else if(game.isLaserdisc())
		{
			medium = Medium.LASERDISC.toString();
		}
		else
		{
			medium = "";
		}

		return medium;
	}

	private String getMedium()
	{
		StringBuilder builder = new StringBuilder();
		if(game.isDisk() || game.isTape())
		{
			builder.append(messages.get(getMedium(game))).append(" ").append(fileGroup.size()).append("x");
		}
		return  builder.toString();
	}

	private List<String> getGenerationList(Game game)
	{
		List<String> generationList = new ArrayList<>();

		if(game.isMSX())
		{
			generationList.add(MSXGeneration.MSX.getDisplayName());
		}
		if(game.isMSX2())
		{
			generationList.add(MSXGeneration.MSX2.getDisplayName());
		}
		if(game.isMSX2Plus())
		{
			generationList.add(MSXGeneration.MSX2Plus.getDisplayName());
		}
		if(game.isTurboR())
		{
			generationList.add(MSXGeneration.TURBO_R.getDisplayName());
		}

		return generationList;
	}

	private String getSound(Game game)
	{
		StringBuilder sound = new StringBuilder("");

		if(game.isPSG())
		{
			sound.append(Sound.PSG.getDisplayName()).append(SEPARATOR);
		}
		if(game.isSCC())
		{
			sound.append(Sound.SCC.getDisplayName()).append(SEPARATOR);
		}
		if(game.isSCCI())
		{
			sound.append(Sound.SCC_I.getDisplayName()).append(SEPARATOR);
		}
		if(game.isPCM())
		{
			sound.append(Sound.PCM.getDisplayName()).append(SEPARATOR);
		}
		if(game.isMSXMUSIC())
		{
			sound.append(Sound.MSX_MUSIC.getDisplayName()).append(SEPARATOR);
		}
		if(game.isMSXAUDIO())
		{
			sound.append(Sound.MSX_AUDIO.getDisplayName()).append(SEPARATOR);
		}
		if(game.isMoonsound())
		{
			sound.append(Sound.MOONSOUND.getDisplayName()).append(SEPARATOR);
		}
		if(game.isMIDI())
		{
			sound.append(Sound.MIDI.getDisplayName()).append(SEPARATOR);
		}

		if(sound.length() > 0)
		{
			sound.setLength(sound.length() - SEPARATOR.length());
		}

		return sound.toString();
	}

	private String getGenre(Game game)
	{
		StringBuilder genre = new StringBuilder("");

		if(game.getGenre1() != null && !game.getGenre1().equals(Genre.UNKNOWN))
		{
			genre.append(game.getGenre1().getDisplayName());

			if(game.getGenre2() != null && !game.getGenre2().equals(Genre.UNKNOWN))
			{
				genre.append(SEPARATOR).append(game.getGenre2().getDisplayName());
			}
		}

		return genre.toString();
	}
}
