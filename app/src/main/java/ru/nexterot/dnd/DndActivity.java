package ru.nexterot.dnd;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import android.content.res.AssetManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;


public class DndActivity extends AppCompatActivity {

    TextView itemName;
    EditText modBonusValue;
    TextView modBonusLabel;
    Button decBonusButton;
    Button incBonusButton;
    Spinner spinner;
    EditText levelValue;
    Button decLevelButton;
    Button incLevelButton;
    TextView maxLevelLabel;


    ArrayList<Item> armours;    // доспехи
    ArrayList<Item> helmets;    // шлемы
    ArrayList<Item> weapons;    // оружие
    ArrayList<Item> sticks;     // палки
    ArrayList<Item> warders;    // жезлы
    ArrayList<Item> staffs;     // посохи
    ArrayList<Item> creeds;     // символы
    ArrayList<Item> spheres;    // сферы
    ArrayList<Item> amulets;    // амулеты
    ArrayList<Item> cloaks;     // плащи
    ArrayList<Item> focusings;  // фокусировки

    ArrayList<Item> foundItems; // случайно выбранные элементы

    Random generator;           // для случайного выбора предметов

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dnd);

        itemName = findViewById(R.id.itemName);
        modBonusValue = findViewById(R.id.modBonusValue);
        modBonusLabel = findViewById(R.id.modBonusLabel);
        decBonusButton = findViewById(R.id.decBonusButton);
        incBonusButton = findViewById(R.id.incBonusButton);
        spinner = findViewById(R.id.itemSpinner);

        levelValue = findViewById(R.id.levelValue);
        decLevelButton = findViewById(R.id.decLevelButton);
        incLevelButton = findViewById(R.id.incLevelButton);
        maxLevelLabel = findViewById(R.id.maxLevelLabel);

        // сначала невидимы
        incLevelButton.setVisibility(View.GONE);
        decLevelButton.setVisibility(View.GONE);
        levelValue.setVisibility(View.GONE);
        maxLevelLabel.setVisibility(View.GONE);

        armours = new ArrayList<>();
        helmets = new ArrayList<>();
        weapons = new ArrayList<>();
        sticks = new ArrayList<>();
        warders = new ArrayList<>();
        staffs = new ArrayList<>();
        creeds = new ArrayList<>();
        spheres = new ArrayList<>();
        amulets = new ArrayList<>();
        cloaks = new ArrayList<>();
        focusings = new ArrayList<>();

        foundItems = new ArrayList<>();

        generator = new Random();

        createItems();

        // адаптер для спиннера выбора предметов
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, Items.items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // привязка обработчика событий
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String groupName = spinner.getSelectedItem().toString();
                if (groupName.compareTo("Предметы на голову") == 0) {
                    maxLevelLabel.setVisibility(View.VISIBLE);
                    decLevelButton.setVisibility(View.VISIBLE);
                    levelValue.setVisibility(View.VISIBLE);
                    incLevelButton.setVisibility(View.VISIBLE);

                    modBonusValue.setVisibility(View.GONE);
                    modBonusLabel.setVisibility(View.GONE);
                    incBonusButton.setVisibility(View.GONE);
                    decBonusButton.setVisibility(View.GONE);
                }
                else {
                    incLevelButton.setVisibility(View.GONE);
                    decLevelButton.setVisibility(View.GONE);
                    levelValue.setVisibility(View.GONE);
                    maxLevelLabel.setVisibility(View.GONE);

                    modBonusValue.setVisibility(View.VISIBLE);
                    modBonusLabel.setVisibility(View.VISIBLE);
                    incBonusButton.setVisibility(View.VISIBLE);
                    decBonusButton.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });
    }

    public void onDecBonusButtonClick(View view) {
        // считываем значение желаемого улучшения
        String bonus = modBonusValue.getEditableText().toString();
        if (bonus.compareTo("1") > 0) {
            String newValue = String.valueOf(Integer.parseInt(bonus) - 1);
            modBonusValue.setText(newValue);
        }
        else {
            modBonusValue.setText("1");
        }
    }

    public void onIncBonusButtonClick(View view) {
        // считываем значение желаемого улучшения
        String bonus = modBonusValue.getEditableText().toString();
        if (bonus.compareTo("6") < 0) {
            String newValue = String.valueOf(Integer.parseInt(bonus) + 1);
            modBonusValue.setText(newValue);
        }
        else {
            modBonusValue.setText("6");
        }
    }

    public void onDecLevelButtonClick(View view) {
        // считываем значение желаемого улучшения
        String bonus = levelValue.getEditableText().toString();
        if (bonus.compareTo("1") > 0) {
            String newValue = String.valueOf(Integer.parseInt(bonus) - 1);
            levelValue.setText(newValue);
        }
        else {
            levelValue.setText("1");
        }
    }

    public void onIncLevelButtonClick(View view) {
        // считываем значение желаемого улучшения
        int bonus = Integer.parseInt(levelValue.getEditableText().toString());
        if (bonus < 30) {
            String newValue = String.valueOf(bonus + 1);
            levelValue.setText(newValue);
        }
        else {
            levelValue.setText("30");
        }
    }

    public void getItemOnClick(View view) {
        // получаем имя группы элементов
        String groupName = spinner.getSelectedItem().toString();

        // форируем строку для вывода
        String resultString = "";

        // будущий выбранный предмет
        Item selectedItem;

        // если выбрано предметы на голову
        if (groupName.compareTo("Предметы на голову") == 0) {
            int maxLevel = Integer.parseInt(levelValue.getText().toString());
            for (Item helmet : helmets) {
                if (Integer.parseInt(((Helmet)helmet).getMinLevel()) <= maxLevel) {
                    foundItems.add(helmet);
                }
            }

            if (foundItems.isEmpty()) {
                itemName.setText("Не найдено");
                // очистим список в котором храним удовлетворяющие нас элементы
                foundItems.clear();
                return;
            } else {
                int random = generator.nextInt();
                int index = Math.abs(random % foundItems.size());
                selectedItem = foundItems.get(index);
                resultString += selectedItem.getName();
                resultString += ", уровень ";
                int itemMinLevel = Integer.parseInt(((Helmet) selectedItem).getMinLevel());
                int level = generator.nextInt(maxLevel - itemMinLevel + 1) + itemMinLevel;
                resultString += level;
            }
        }
        // если выбраны остальные
        else {
            // считываем значение желаемого улучшения
            String bonus = modBonusValue.getEditableText().toString();

            ArrayList<Item> group;
            switch (groupName) {
                case "Оружие":
                    group = weapons;
                    break;
                case "Доспехи":
                    group = armours;
                    break;
                case "Волшебные палочки":
                    group = sticks;
                    break;
                case "Жезлы":
                    group = warders;
                    break;
                case "Посохи":
                    group = staffs;
                    break;
                case "Символы веры":
                    group = creeds;
                    break;
                case "Сферы":
                    group = spheres;
                    break;
                case "Плащи":
                    group = cloaks;
                    break;
                case "Амулеты":
                    group = amulets;
                    break;
                case "Фокусировки":
                    group = focusings;
                    break;
                default:
                    itemName.setText("Ошибка!");
                    return;
            }

            for (Item item : group) {
                if (bonus.compareTo(item.getMinBonus()) >= 0 && bonus.compareTo(item.getMaxBonus()) <= 0) {
                    foundItems.add(item);
                }
            }

            if (foundItems.isEmpty()) {
                itemName.setText("Не найдено");
                // очистим список в котором храним удовлетворяющие нас элементы
                foundItems.clear();
                return;
            } else {
                // получаем случайный элемент из списка найденных
                int random = generator.nextInt();
                int index = Math.abs(random % foundItems.size());
                selectedItem = foundItems.get(index);

                resultString += selectedItem.getName();
                resultString += " +";
                resultString += bonus;

                // если доспех, добавить материал (латный, тканевый и т.д.)
                if (selectedItem instanceof Armour) {
                    ArrayList<String> qualities = ((Armour) selectedItem).getQualities();
                    int qualityIndex = Math.abs(random % qualities.size());
                    resultString = qualities.get(qualityIndex) + " " + resultString;
                    System.out.println(resultString);
                }
                // или если оружие, выбрать доступную ему группу (тяжелый клинок, праща, и т.п.)
                else if (selectedItem instanceof Weapon) {
                    ArrayList<String> groups = ((Weapon) selectedItem).getGroups();
                    String itemGroup = "";
                    // если любая группа
                    if (groups.size() == 1 && groups.get(0).compareTo("любое") == 0) {
                        int randomGroupIndex = Math.abs(generator.nextInt() % Items.weaponGroups.length);
                        itemGroup = Items.weaponGroups[randomGroupIndex];
                    }
                    // если любое дальнобойное
                    else if (groups.size() == 1 && groups.get(0).compareTo("любое дальнобойное") == 0) {
                        itemGroup = "любое дальнобойное";
                    }
                    // если любое рукопашное
                    else if (groups.size() == 1 && groups.get(0).compareTo("любое рукопашное") == 0) {
                        itemGroup = "любое рукопашное";
                    } else {
                        int randomGroupIndex = Math.abs(generator.nextInt() % groups.size());
                        itemGroup = groups.get(randomGroupIndex);
                    }

                    String[] weaponsChoice = Items.distantWeapons; // шоб не ругалась IDE
                    // выбрать конкретное оружие
                    switch (itemGroup) {
                        case "любое дальнобойное":
                            weaponsChoice = Items.distantWeapons;
                            break;
                        case "любое рукопашное":
                            weaponsChoice = Items.meleeWeapons;
                            break;
                        case "арбалет":
                            weaponsChoice = Items.arbalests;
                            break;
                        case "посох":
                            weaponsChoice = Items.warStaffs;
                            break;
                        case "булава":
                            weaponsChoice = Items.maces;
                            break;
                        case "древковое оружие":
                            weaponsChoice = Items.shafts;
                            break;
                        case "кирка":
                            weaponsChoice = Items.pickaxes;
                            break;
                        case "копье":
                            weaponsChoice = Items.spears;
                            break;
                        case "легкий клинок":
                            weaponsChoice = Items.lightblades;
                            break;
                        case "лук":
                            weaponsChoice = Items.bows;
                            break;
                        case "молот":
                            weaponsChoice = Items.hammers;
                            break;
                        case "праща":
                            weaponsChoice = Items.slings;
                            break;
                        case "топор":
                            weaponsChoice = Items.axes;
                            break;
                        case "тяжелый клинок":
                            weaponsChoice = Items.hardblades;
                            break;
                        case "цеп":
                            weaponsChoice = Items.flails;
                            break;
                        case "кинжал":
                            weaponsChoice = Items.daggers;
                            break;
                    }

                    int randomIndex = Math.abs(generator.nextInt() % weaponsChoice.length);
                    resultString = weaponsChoice[randomIndex] + " - " + resultString;
                }
            }
        }

        // общая часть
        resultString += "\n"+"книга №";
        resultString += selectedItem.getBookNumber();
        resultString += ", страница ";
        resultString += selectedItem.getBookPage();

        // выводим результат
        itemName.setText(resultString);

        // очистим список в котором храним удовлетворяющие нас элементы
        foundItems.clear();
    }

    private void createItems() {
        AssetManager am = getApplicationContext().getAssets();

        // прочитать однотипные
        String[] filenames = {"sticks.txt", "warders.txt", "staffs.txt", "creeds.txt",
                "spheres.txt", "amulets.txt", "cloaks.txt", "focusings.txt"};
        ArrayList<ArrayList<Item>> groups = new ArrayList<>();
        groups.add(sticks);
        groups.add(warders);
        groups.add(staffs);
        groups.add(creeds);
        groups.add(spheres);
        groups.add(amulets);
        groups.add(cloaks);
        groups.add(focusings);

        for (int i = 0; i < filenames.length; i++) {
            String filename = filenames[i];
            ArrayList<Item> group = groups.get(i);
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(am.open(filename)));
                String line;
                while((line = br.readLine()) != null) {
                    String[] attrs = line.split("/");
                    if (attrs.length != 5) {
                        itemName.setText("Error when parsing " + filename + "!");
                    } else {
                        Item item = new Item(attrs[0], attrs[1], attrs[2], attrs[3], attrs[4]);
                        group.add(item);
                    }

                }

            } catch (IOException e) {
                itemName.setText("Error! " + filename + " not found!");
            }
        }

        // прочитать броню
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(am.open("armours.txt")));
            String line;
            while((line = br.readLine()) != null) {
                String[] attrs = line.split("/");

                ArrayList<String> qualitiesList = new ArrayList<>(Arrays.asList(attrs[1].split(",")));

                if (attrs.length != 6) {
                    itemName.setText("Error when parsing armours.txt!");
                } else {
                    Item item = new Armour(attrs[0], attrs[2], attrs[3], attrs[4], attrs[5], qualitiesList);
                    armours.add(item);
                }

            }

        } catch (IOException e) {
            itemName.setText("Error! armours.txt not found!");
        }

        // прочитать оружие
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(am.open("weapons.txt")));
            String line;
            while((line = br.readLine()) != null) {
                String[] attrs = line.split("/");

                ArrayList<String> qroupsList = new ArrayList<>(Arrays.asList(attrs[1].split(",")));

                if (attrs.length != 6) {
                    itemName.setText("Error when parsing weapons.txt!");
                } else {
                    Item item = new Weapon(attrs[0], attrs[2], attrs[3], attrs[4], attrs[5], qroupsList);
                    weapons.add(item);
                }

            }

        } catch (IOException e) {
            itemName.setText("Error! weapons.txt not found!");
        }

        // прочитать шлемы
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(am.open("helmets.txt")));
            String line;
            while((line = br.readLine()) != null) {
                String[] attrs = line.split("/");
                if (attrs.length != 4) {
                    itemName.setText("Error when parsing helmets.txt!");
                } else {
                    Item helmet = new Helmet(attrs[0], attrs[1], attrs[2], attrs[3]);
                    helmets.add(helmet);
                }

            }

        } catch (IOException e) {
            itemName.setText("Error! helmets.txt not found!");
        }
    }
}


class Item {
    private String name;
    private String minBonus;
    private String maxBonus;
    private String bookNumber;
    private String bookPage;

    public String getName() {
        return name;
    }

    public String getMinBonus() {
        return minBonus;
    }

    public String getMaxBonus() {
        return maxBonus;
    }

    public String getBookNumber() {
        return bookNumber;
    }

    public String getBookPage() {
        return bookPage;
    }

    Item(String name, String minBonus, String maxBonus, String bookNumber, String bookPage) {
        this.name = name;
        this.minBonus = minBonus;
        this.maxBonus = maxBonus;
        this.bookNumber = bookNumber;
        this.bookPage = bookPage;
    }
}


class Armour extends Item {
    private ArrayList<String> qualities;

    ArrayList<String> getQualities() {
        return qualities;
    }

    Armour(String name, String minBonus, String maxBonus, String bookNumber, String bookPage,
                  ArrayList<String> qualities) {
        super(name, minBonus, maxBonus, bookNumber, bookPage);
        this.qualities = qualities;
    }
}


class Weapon extends Item {
    private ArrayList<String> groups;

    ArrayList<String> getGroups() {
        return groups;
    }

    Weapon(String name, String minBonus, String maxBonus, String bookNumber, String bookPage,
                  ArrayList<String> groups) {
        super(name, minBonus, maxBonus, bookNumber, bookPage);
        this.groups = groups;
    }
}


class Helmet extends Item {
    private String minLevel;

    String getMinLevel() {
        return minLevel;
    }

    Helmet(String name, String minLevel, String bookNumber, String bookPage) {
        super(name, "", "", bookNumber, bookPage);
        this.minLevel = minLevel;
    }
}


class Items {
    static String[] items = {"Оружие", "Доспехи", "Волшебные палочки", "Жезлы", "Посохи", "Символы веры",
            "Сферы", "Амулеты", "Плащи", "Фокусировки", "Предметы на голову"};

    static String[] stages = {"героический этап", "этап совершенства", "эпический этап"};

    static String[] weaponGroups = {"арбалет", "посох", "булава", "древковое оружие", "кирка",
            "копье", "легкий клинок", "лук", "молот", "праща", "топор", "тяжелый клинок", "цеп"};

    static String[] distantWeapons = {"праща", "ручной арбалет", "арбалет", "длинный лук", "короткий лук",
            "сюрикен"};

    static String[] meleeWeapons = {"посох", "булава", "дубинка", "моргенштерн", "палица", "алебарда",
            "глефа", "длинное копье", "боевая кирка", "копье", "метательное копье", "кинжал",
            "серп", "короткий меч", "рапира", "катар", "боевой молот", "метательный молот",
            "кувалда", "боевой топор", "ручной топор", "секира", "коса", "длинный меч", "скимитар",
            "фальчион", "полуторный меч", "цеп", "тяжелый цеп", "шипованная цепь"};

    static String[] arbalests = {"ручной аралет", "арбалет"};
    static String[] warStaffs = {"посох"};
    static String[] maces = {"булава", "дубинка", "моргенштерн", "палица"};
    static String[] shafts = {"алебарда", "глефа", "длинное копье"};
    static String[] pickaxes = {"боевая кирка"};
    static String[] spears = {"копье", "метательное копье", "длинное копье"};
    static String[] lightblades = {"кинжал", "серп", "короткий меч", "рапира", "катар", "сюрикен"};
    static String[] bows = {"длинный лук", "короткий лук"};
    static String[] hammers = {"боевой молот", "метательный молот", "кувалда"};
    static String[] slings = {"праща"};
    static String[] axes = {"боевой топор", "ручной топор", "секира", "алебарда"};
    static String[] hardblades = {"коса", "длинный меч", "скимитар", "глефа", "фальчион", "полуторный меч"};
    static String[] flails = {"цеп", "тяжелый цеп", "шипованная цепь"};
    static String[] daggers = {"кинжал"};
}