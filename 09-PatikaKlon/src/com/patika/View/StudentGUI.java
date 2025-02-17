package com.patika.View;

import com.patika.Helper.Config;
import com.patika.Helper.Helper;
import com.patika.Model.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class StudentGUI extends JFrame {
    private JPanel wrapper;
    private JTabbedPane pnl_patika;
    private JTable tbl_patika_list;
    private JLabel lbl_student_welcome;
    private JPanel scrl_patika_list;
    private JPanel pnl_patika_registry;
    private JTextField fld_patika_name;
    private JButton btn_patika_register;
    private JPanel pnl_register_patika;
    private JScrollPane scrl_register_patika;
    private JTable tbl_register_patika;
    private JPanel pnl_course_content;
    private JScrollPane scrl_course_content;
    private JTable tbl_course_content;
    private JPanel pnl_course_raiting;
    private JComboBox cmb_course_raiting;
    private JButton btn_course_raiting;
    private JScrollPane scrl_quiz;
    private DefaultTableModel mdl_patika_list;
    private Object[] row_patika_list;
    private DefaultTableModel mdl_regiter_patika_list;
    private Object[] row_register_patika_list;
    private DefaultTableModel mdl_regiter_course_list;
    private Object[] row_register_course_list;
    private final User user;
    private JLabel lbl_course_list_name;
    private JPanel pnl_course_list;
    private JTable tbl_register_course_list;
    private JScrollPane scrl_course_list;
    private JPanel pnl_register_patika_top;
    private JPanel pnl_register_patika_bottom;
    private JScrollPane scrl_register_patika_bottom;
    private JPanel pnl_course_continue;
    private JLabel lbl_course_id;
    private JButton btn_course_continue;
    private JButton btn_logout;
    private JTextArea textArea1;
    private DefaultTableModel mdl_course_content_list;
    private Object[] row_course_content_list;
    private JTextArea txt_course_content;
    private JButton btn_finish_course;
    private JTextArea txt_quiz;
    private JRadioButton radioButton1;
    private JRadioButton radioButton2;
    private JRadioButton radioButton3;
    private JRadioButton radioButton4;
    private JButton button1;
    private ButtonGroup g1;
    private String answerQuiz;
    private JPanel pnl_quiz;
    private int content_id;
    private int student_course_content_id;
    private boolean quizAnswer;
    private String selectedQuizId = null;




    private ArrayList<StudentPatika> user_register_patika;

    public StudentGUI(User user){
        this.user = user;
        add(wrapper);
        setSize(1024,860);
        int x = Helper.screenCenter("x",getSize());
        int y = Helper.screenCenter("y",getSize());
        setLocation(x,y);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setTitle(Config.PROJECT_TITLE);
        setVisible(true);

        lbl_student_welcome.setText(user.getName() + " patika sistemine hoş geldiniz. Ders seçimi yapabilirsiniz.");

        mdl_patika_list = new DefaultTableModel();
        Object[] col_patika_list = {"Id","Name"};
        mdl_patika_list.setColumnIdentifiers(col_patika_list);
        row_patika_list = new Object[col_patika_list.length];
        tbl_patika_list.setModel(mdl_patika_list);
        tbl_patika_list.getColumnModel().getColumn(0).setMaxWidth(70);
        loadPatikaList();
        user_register_patika = StudentPatika.getRegisterPatika(user.getId());

        mdl_regiter_patika_list = new DefaultTableModel();
        Object[] col_register_patika_list = {"Id","Name"};
        mdl_regiter_patika_list.setColumnIdentifiers(col_patika_list);
        row_register_patika_list = new Object[col_register_patika_list.length];
        tbl_register_patika.setModel(mdl_regiter_patika_list);
        tbl_register_patika.getColumnModel().getColumn(0).setMaxWidth(70);
        if(!StudentCourse.getAll(user.getId()).isEmpty()){
            loadAllList();
        }

        g1 = new ButtonGroup();
        g1.add(radioButton1);
        g1.add(radioButton2);
        g1.add(radioButton3);
        g1.add(radioButton4);



        mdl_regiter_course_list = new DefaultTableModel();
        Object[] col_register_course_list = {"Id","Patika","Kurs Adı","Tamamlandı mı?"};
        mdl_regiter_course_list.setColumnIdentifiers(col_register_course_list);
        row_register_course_list = new Object[col_register_course_list.length];
        tbl_register_course_list.setModel(mdl_regiter_course_list);
        tbl_register_course_list.getColumnModel().getColumn(0).setMaxWidth(70);

        tbl_patika_list.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                var selectedPatika = tbl_patika_list.getValueAt(tbl_patika_list.getSelectedRow(),0).toString();
                fld_patika_name.setText(selectedPatika);
            }
        });

        mdl_course_content_list = new DefaultTableModel();
        Object[] col_course_content_list = {"Id","Ders Kodu","Ders Adı","Kurs Adı","Bitirme durumu","Verilen Oy"};
        mdl_course_content_list.setColumnIdentifiers(col_course_content_list);
        row_course_content_list = new Object[col_course_content_list.length];
        tbl_course_content.setModel(mdl_course_content_list);
        loadContentList();
        tbl_course_content.getColumnModel().getColumn(0).setMaxWidth(70);

        btn_patika_register.addActionListener(e -> {
            int patika_id = Integer.parseInt(fld_patika_name.getText());

            var isRegister = user_register_patika.stream().filter(o->o.getPatika_id() == patika_id).findAny().isEmpty();
            var quizes = Quiz.getList();
            if (isRegister){
                if (StudentPatika.add(patika_id,user.getId())) {
                    Helper.showMsg("done");
                    var courses = Course.getListByPatikaId(patika_id);
                    ArrayList<CourseContent> contents = new ArrayList<>();
                    courses.forEach(o->{
                        var c = CourseContent.getListByCourseId(o.getId());
                        StudentCourse.add(user.getId(),o.getId(),false);
                        contents.addAll(c);
                    });

                    user_register_patika = StudentPatika.getRegisterPatika(user.getId());

                    contents.forEach(c->{
                        StudentCourseContent.add(user.getId(),c.getId(),false);
                        quizes.forEach(q->{
                            var q1 = Quiz.getFetch(q.getId());
                            if(q.getCourse_content_id()==c.getId() && q1!=null){
                                StudentCourseQuiz.add(user.getId(),c.getId(),false);
                            }
                        });

                    });
                    loadAllList();
                    loadContentList();
                }
            }else {
                Helper.showMsg("Derse kaydınız bulunmakta.");
            }

        });
        btn_logout.addActionListener(e -> {
            closeScreen();
        });
        btn_course_continue.addActionListener(e->{
            int id = Integer.parseInt(lbl_course_id.getText());
            pnl_patika.setSelectedIndex(2);
            loadContentContinueList(id);

        });
        tbl_register_patika.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                var id  = tbl_register_patika.getValueAt(tbl_register_patika.getSelectedRow(),0).toString();
                loadRegisterCourseList(Integer.parseInt(id));
            }
        });

        tbl_register_course_list.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                var id = tbl_register_course_list.getValueAt(tbl_register_course_list.getSelectedRow(),0).toString();
                lbl_course_id.setText(id);
            }
        });
        pnl_quiz.setVisible(false);
        tbl_course_content.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                var scq = StudentCourseQuiz.getList();
                content_id = Integer.parseInt(tbl_course_content.getValueAt(tbl_course_content.getSelectedRow(),0).toString());
                student_course_content_id = Integer.parseInt(tbl_course_content.getValueAt(tbl_course_content.getSelectedRow(),1).toString());
                var durum = tbl_course_content.getValueAt(tbl_course_content.getSelectedRow(),4);
                var raiting = tbl_course_content.getValueAt(tbl_course_content.getSelectedRow(),5);
                if (durum.toString().equals("false")){
                    if (raiting.equals(0)){
                        btn_course_raiting.setEnabled(true);
                        btn_finish_course.setEnabled(false);
                    }else {
                        btn_course_raiting.setEnabled(false);
                        btn_finish_course.setEnabled(true);
                    }
                }else {
                    btn_finish_course.setEnabled(false);
                }

                System.out.println(raiting);
                var contents = StudentCourseContent.getAll(user.getId());
                var icerik = contents.stream().filter(o->o.getCourseContent().getId()==content_id).findAny();
                var c = icerik.get().getCourseContent().getDescription();
                var quiz = Quiz.getList();
                txt_course_content.setText(c+("\n"+icerik.get().getCourseContent().getYoutube()));
                scq.forEach(a->{
                    if(content_id==a.getQuiz().getCourse_content_id()){
                        if (a.getIsSuccess()==true){
                            button1.setText("Cevaplandı");
                            button1.setEnabled(false);
                            radioButton1.setEnabled(false);
                            radioButton2.setEnabled(false);
                            radioButton3.setEnabled(false);
                            radioButton4.setEnabled(false);
                        }
                        pnl_quiz.setVisible(true);
                        txt_quiz.setText(a.getQuiz().getQuestion());
                        radioButton1.setText(a.getQuiz().getUser_answer1());
                        radioButton2.setText(a.getQuiz().getUser_answer2());
                        radioButton3.setText(a.getQuiz().getUser_answer3());
                        radioButton4.setText(a.getQuiz().getUser_answer4());
                        answerQuiz = a.getQuiz().getAnswer();
                        selectedQuizId = String.valueOf(a.getId());

                    }else {
                        button1.setEnabled(true);
                        pnl_quiz.setVisible(false);
                        txt_quiz.setText(null);
                        radioButton1.setText(null);
                        radioButton2.setText(null);
                        radioButton3.setText(null);
                        radioButton4.setText(null);
                        selectedQuizId = null;
                    }
                });
            }
        });

        btn_course_raiting.addActionListener(e->{
            int raiting = Integer.parseInt(cmb_course_raiting.getSelectedItem().toString());
            StudentCourseContent.updateRaiting(student_course_content_id, raiting);
            btn_finish_course.setEnabled(true);
            btn_course_raiting.setEnabled(false);
            loadContentList();
        });

        button1.addActionListener(e-> {
            if (radioButton1.isSelected()) {
                if (answerQuiz(answerQuiz,radioButton1.getText())){

                }
            } else if (radioButton2.isSelected()) {
                if (answerQuiz(answerQuiz,radioButton2.getText())){

                }
            } else if (radioButton3.isSelected()) {
                if (answerQuiz(answerQuiz,radioButton3.getText())){

                }
            } else if (radioButton4.isSelected()) {
                if (answerQuiz(answerQuiz,radioButton4.getText())){

                }
            }else {
                Helper.showMsg("fill");
            }
            if(quizAnswer){
                System.out.println("cevap işlendi");
                int quiz_id = Integer.parseInt(selectedQuizId);
                System.out.println(quiz_id);
                StudentCourseQuiz.update(quiz_id,true);

            }
        });

        btn_finish_course.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                StudentCourseContent.updateSuccess(student_course_content_id,true);
                loadContentList();
                btn_finish_course.setEnabled(false);
            }
        });
    }
    private boolean answerQuiz(String answer,String question){
        quizAnswer = false;
        if (question.equals(answer)){
            quizAnswer =true;
            Helper.showMsg("Cevap Doğru");
        }else {
            Helper.showMsg("Yanlış Cevap");
        }
        return quizAnswer;
    }


    private void loadContentContinueList(int id) {
        clearModel(tbl_course_content);
        int i = 0;

        for (var scc: StudentCourseContent.getAll(user.getId())){
            if(scc.getCourseContent().getCourse().getId()==id){

                i = 0;
                row_course_content_list[i++] = scc.getCourse_content_id();
                row_course_content_list[i++] = scc.getId();
                row_course_content_list[i++] = scc.getCourseContent().getTitle();
                row_course_content_list[i++] = scc.getCourseContent().getCourse().getName();
                row_course_content_list[i++] = scc.getIsSuccess();
                mdl_course_content_list.addRow(row_course_content_list);
            }
        }
    }

    private void closeScreen(){
        if (Helper.confirm("sure")){
            dispose();
            LoginGUI l = new LoginGUI();
        }
    }

    private void loadAllList(){
        loadRegisterPatikaList();
        loadPatikaList();

    }
    private void loadContentList(){
        clearModel(tbl_course_content);
        int i = 0;

        for (var scc: StudentCourseContent.getAll(user.getId())){
            i = 0;
            row_course_content_list[i++] = scc.getCourse_content_id();
            row_course_content_list[i++] = scc.getId();
            row_course_content_list[i++] = scc.getCourseContent().getTitle();
            row_course_content_list[i++] = scc.getCourseContent().getCourse().getName();
            row_course_content_list[i++] = scc.getIsSuccess();
            row_course_content_list[i++] = scc.getRaiting();
            mdl_course_content_list.addRow(row_course_content_list);

        }
    }
    private void loadRegisterCourseList(int id){
        clearModel(tbl_register_course_list);
        int i = 0;
        var rc = StudentCourse.getAll(user.getId());
        for (var rcl : StudentCourse.getAll(user.getId())){
            if(rcl.getCourse().getPatika().getId()==id){
                i =0;
                row_register_course_list[i++] = rcl.getCourse().getId();
                row_register_course_list[i++] = rcl.getCourse().getPatika().getName();
                row_register_course_list[i++] = rcl.getCourse().getName();
                row_register_course_list[i++] = rcl.getIsSuccess();
                mdl_regiter_course_list.addRow(row_register_course_list);
            }

        }

    }

    private void loadPatikaList() {
        clearModel(tbl_patika_list);
        int i =0;
        for (Patika p : Patika.getList()){
            i =0;
            row_patika_list[i++] = p.getId();
            row_patika_list[i++] = p.getName();
            mdl_patika_list.addRow(row_patika_list);
        }
    }

    private void loadRegisterPatikaList(){
        clearModel(tbl_register_patika);
        int i =0;

        for (var patika : user_register_patika){
            i = 0;
            row_register_patika_list[i++] = patika.getPatika().getId();
            row_register_patika_list[i++] = patika.getPatika().getName();
            mdl_regiter_patika_list.addRow(row_register_patika_list);
        }

    }
    private void clearModel(JTable model){
        DefaultTableModel clearModel = (DefaultTableModel) model.getModel();
        clearModel.setRowCount(0);
    }

    public static void main(String[] args) {
        Helper.setLayout();
        StudentGUI sg = new StudentGUI(new User(1,"Ali","ali","123","student"));
    }

}
