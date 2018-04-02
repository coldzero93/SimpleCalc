import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Stack;

public class SimpleCalc extends JFrame {

	private static final long serialVersionUID = 1L;
	
	private boolean isInit = true; // ������ �ʱ�ȭ ���¸� ��Ÿ���� ����
	private boolean isDotEnabled = true; // ��Ʈ�� ����� �� �ִ��� ��Ÿ���� ����
	private Font font = new Font("arian", Font.BOLD, 20); // ���� ��ư���� �⺻ ��Ʈ
	private String[] buttonVal = {"7", "8", "9", "*", // ���� ��ư���� ���� ������� ǥ��
			  			  "4", "5", "6", "��", 
			  			  "1", "2", "3", "+", 
			  			  "0", ".", "=", "-"  };
	private String operator = "+-*��"; // �����ڿ� �ش��ϴ� ���ڿ�
	private int numLeftPar;  // �Էµ� ���� ��ȣ�� ����
	private int numRightPar; // �Էµ� ������ ��ȣ�� ����
	
	private JTextField result = new JTextField("0", 22); // ����� ������� ��Ÿ���� �ؽ�Ʈ �ʵ�
	private JButton del = new JButton("��");				 // ���� ��ư
	private JButton clear = new JButton("  C  ");		 // �ʱ�ȭ ��ư
	private JButton parenthesis = new JButton(" ( ) ");	 // ��ȣ ��ư
	private JButton[] button = new JButton[16];			 // �ǿ����� �� ������ ��ư��
	private MyListener listener = new MyListener();		 // ��ư�� ��� ������ ���� ������
	
	// ���� â�� �����ϴ� �޼ҵ�
	public void SwingFrame() {
		
		Panel1 p1 = new Panel1(); // ����� �ؽ�Ʈ�ʵ�, ���� ��ư, �ʱ�ȭ ��ư�� ��ġ�� �г�1
		Panel2 p2 = new Panel2(); // �ǿ����� �� ������ ��ư���� ��ġ�� �г�2
		
		add(p1);
		add(p2);
		
		setLayout(null);
		setSize(700, 500);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setTitle("SimpleCalc");
		setVisible(true);
	}
	
	// ����� �ؽ�Ʈ�ʵ�, ���� ��ư, �ʱ�ȭ ��ư�� ��ġ�� �г�1
	private class Panel1 extends JPanel {

		private static final long serialVersionUID = 1L;

		public Panel1() {
			
			setBounds(40, 50, 600, 50);
			setBackground(Color.YELLOW);
			
			// ����� �ؽ�Ʈ �ʵ带 ������ �Ұ����ϵ��� �ϸ�, ���� ������ ����
			result.setEnabled(false);
			result.setPreferredSize(new Dimension(80, 40));
			result.setHorizontalAlignment(JTextField.RIGHT);
			
			// ��Ʈ ����
			result.setFont(font);
			del.setFont(font);
			clear.setFont(font);
			parenthesis.setFont(font);
			
			// ������ ����
			result.addActionListener(listener);
			del.addActionListener(listener);
			clear.addActionListener(listener);
			parenthesis.addActionListener(listener);
			
			// �гο� �߰�
			add(result);
			add(del);
			add(clear);
			add(parenthesis);
		}
	}
	
	// �ǿ����� �� ������ ��ư���� ��ġ�� �г�2
	private class Panel2 extends JPanel {

		private static final long serialVersionUID = 1L;

		public Panel2() {
			
			setBounds(40, 120, 600, 300);
			setBackground(Color.BLUE);
			setLayout(new GridLayout(4, 4));
			
			// ������ ������ ������� ��ư�� �гο� �߰��ϸ�, ���� ��Ʈ ���� �� ������ ����
			for(int i=0; i<16; i++) {
				button[i] = new JButton(buttonVal[i]);
				button[i].setFont(font);
				button[i].addActionListener(listener);
				add(button[i]);
			}
		}
	}
	
	// �ش� ���ڿ��� ������(+, -, *, /, .)���� �Ǻ��ϴ� �޼ҵ�
	private boolean isOperator(CharSequence cs) { return operator.contains(cs); }
	
	// ���� ǥ����� ���� ǥ������� ��ȯ�ϴ� �޼ҵ�
	private String[] toPostfix(String infix) {
		
		Stack<String> stack = new Stack<String>(); // ���� ����
		
		if(infix.charAt(0) == '-') infix = "0" + infix; // �� ���� -�� ���, �� �տ� 0�� �߰��� �Է��Ͽ� ������ ����
		
		// ������ ��� �տ� 0�� �߰��Ͽ� -�������� �νĵǵ��� ����
		char cur, next;
		for(int i=0; i<infix.length()-1; i++) {
			cur = infix.charAt(i);
			next = infix.charAt(i+1);
			if(next == '-' && !(cur >= '0' && cur <= '9')) {
				infix = infix.substring(0, i+1) + "0" + infix.substring(i+1, infix.length());
				i++;
			}
		}
		
		// �ǿ����ڿ� �����ڸ� �����ϱ� ���� ��ó�� (+�� *�� ����ǥ���Ŀ� �߿��� ����� �����ϴ� �����̹Ƿ� []�� ó���Ͽ� ����)
		infix = infix.replaceAll("[+]", " + ");
		infix = infix.replaceAll("-", " - ");
		infix = infix.replaceAll("[*]", " * ");
		infix = infix.replaceAll("��", " �� ");
		infix = infix.replaceAll("\\(", "( ");
		infix = infix.replaceAll("\\)", " )");
		
		String[] tokens = infix.split(" "); // �ǿ����ڿ� �����ڸ� �����Ͽ� ���ڿ� �迭�� ����
		String[] postfix = new String[tokens.length - (numLeftPar + numRightPar)]; // ���� ǥ����� ���� �迭 ����
		int pos = 0;
		
		// �ǿ������� ��� ������ ���, �������� ��� �ڽź��� �켱������ ���� �����ڰ� ���ÿ��� peek�Ǵ� ��쿡�� push�ϸ� ������ ���� pop
		for(int i=0; i<tokens.length; i++) {
			if(tokens[i].equals("(")) stack.push("("); // ���� ��ȣ�� ������ ���ÿ� push
			else if(tokens[i].equals(")")) { // ������ ��ȣ�� ������ ���� ��ȣ�� ���� ������ ������ pop�Ͽ� ���
				while(!stack.isEmpty() && !stack.peek().equals("("))
					postfix[pos++] = stack.pop();
				stack.pop(); // ���ÿ� �ִ� ���� ��ȣ 1�� ����
			} else if(tokens[i].equals("*") || tokens[i].equals("��")) { // *�� /�� ������ ������ ������� �ʰ�, peek�� ���� *�� /��� ������ pop�Ͽ� ����� �ݺ�, ���� ���ÿ� push
				while(!stack.isEmpty() && (stack.peek().equals("*") || stack.peek().equals("��")))
					postfix[pos++] = stack.pop();
				stack.push(tokens[i]);
			} else if(tokens[i].equals("+") || tokens[i].equals("-")) { // +�� -�� ������ ������ ������� �ʰ�, peek�� ���� �����ڶ�� ������ pop�Ͽ� ����� �ݺ�, ���� ���ÿ� push
				while(!stack.isEmpty() && isOperator(stack.peek()))
					postfix[pos++] = stack.pop();
				stack.push(tokens[i]);
			} else postfix[pos++] = tokens[i]; // �ǿ����ڴ� ������ ���
		}
		
		// ���� �ִ� �����ڳ� ��ȣ pop�Ͽ� ���
		while(!stack.isEmpty()) postfix[pos++] = stack.pop();
				
		// ���� ǥ��� ��ȯ
		return postfix;
	}
	
	// ���� ǥ����� ����ϴ� �޼ҵ�
	private double calcPost(String[] postfix) {
	
		
		Stack<Double> stack = new Stack<Double>(); // ���� ����
		double a, b; // ����� ���� ���� ����
		
		// �ǿ����ڴ� ���ÿ� push�ϸ�, �����ڰ� ���� ��� ���ÿ��� �ΰ��� ���� pop�ؿ� ��� �� �ٽ� push
		for(int i=0; i<postfix.length; i++) {
			if(isOperator(postfix[i])) {
				b = stack.pop(); // ������ ���Լ����̹Ƿ� �������� b, a�� �Ҵ����ִ� ���� �߿�
				a = stack.pop();
				switch(postfix[i]) {
				case "+" : 
					stack.push(a + b);
					break;
				case "-" : 
					stack.push(a - b);
					break;
				case "*" : 
					stack.push(a * b);
					break;
				case "��" : 
					stack.push(a / b);
					break;
				}
			} else {
				stack.push(Double.parseDouble(postfix[i]));
			}
		}
		
		return stack.pop(); // ���� ��� ����� ���ÿ��� pop�Ͽ� ��ȯ
	}
	
	// �� ��ư�� ��� ������ ��Ÿ���� ������ ����
	private class MyListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			
			String clicked = ((JButton)e.getSource()).getText(); // Ŭ���� ���� �������� �ľ�
			
			String origin = result.getText(); // ���� �ؽ�Ʈ ����
			String lastStr = Character.toString(origin.charAt(origin.length()-1)); // ���� �ؽ�Ʈ�� ������ ���ڿ� ����
			
			// 1. �ʱ�ȭ(C) ��ư�� ���� ���
			if(clicked.equals("  C  ")) {
				
				result.setText("0"); // �Է� ���� 0���� ���� ��
				isInit = true;		 // �ʱ�ȭ ���·� ����
				isDotEnabled = true; // ��Ʈ ����� �����ϵ��� �ʱ�ȭ
				numLeftPar = numRightPar = 0; // ��ȣ ���� �ʱ�ȭ
			
			// 2. ����(��) ��ư�� ���� ���
			} else if(clicked.equals("��")) {
				
				result.setText(origin.substring(0, origin.length()-1)); // �Է� ������ ������ ���ڿ��� ����
				
				if(result.getText().equals("")) { // �� ������ ����� �ƹ� ���� ���ٸ�
					result.setText("0"); 		  // �Է� ���� 0���� ���� ��
					isInit = true;				  // �ʱ�ȭ ���·� ����
				}
				
				// ��ȣ�� ������ ���, �Էµ� ��ȣ�� ���� ����
				if(lastStr.equals("(")) numLeftPar--;
				else if(lastStr.equals(")")) numRightPar--;
				
				if(lastStr.equals(".")) isDotEnabled = true; // ���������� ���� ���ڿ��� ��Ʈ��, ��Ʈ ����� �����ϵ��� �ʱ�ȭ
				else if(isOperator(lastStr)) {
					String nowStr = result.getText();
					String nowChar;
					isDotEnabled = true;
					for(int i=nowStr.length()-1; i>=0; i--) {
						nowChar = Character.toString(nowStr.charAt(i));
						if(isOperator(nowChar)) break;
						else if(nowChar.equals(".")) {
							isDotEnabled = false;
							break;
						}
					}
				}
			
			// 3. ��ȣ ��ư�� ���� ���
			} else if(clicked.equals(" ( ) ")) {

				if(isInit) { // (Left) �ʱ� ������ ���
					result.setText("(");
					numLeftPar++;
					isInit = false;
				} else if(lastStr.equals("(")) { // (Left) ������ �Է� ���� ���� ��ȣ�� ���
					result.setText(result.getText() + "(");
					numLeftPar++;
				} else {  // �������� ���
//					if(isOperator(lastStr) || lastStr.equals("."))  // ������ �Է� ���� ������ ��Ʈ�� ���
//						result.setText(origin.substring(0, origin.length()-1)); // �Է� ������ ������ ���ڿ��� ����
					if(numLeftPar == numRightPar) {  // *(Left) ��ȣ�� ��� ���� ���
						if(isOperator(lastStr)) result.setText(result.getText() + "(");
						else result.setText(result.getText() + "*(");
						numLeftPar++;
					} else { // (Right) ��ȣ�� �ϳ��� ���� ���
						result.setText(result.getText() + ")");
						numRightPar++;
					}
					
				}
				
			// 4. ���(=) ��ư�� ���� ���
			} else if(clicked.equals("=")) {
				
				// ��ȣ�� �� ������ ���, ���� �ݾ���
				while(numLeftPar > numRightPar) {
					result.setText(result.getText() + ")");
					numRightPar++;
				}
				
				if(isOperator(lastStr)) result.setText(origin.substring(0, origin.length()-1)); // ������ ���ڿ��� �����ڸ� ����
				String[] postfix = toPostfix(result.getText()); // ���� ǥ����� ���� ǥ������� ��ȯ
				double calcResult = calcPost(postfix); // ���� ǥ����� ���
				
				// �Ҽ����� ��Ÿ�� �ʿ䰡 ���ٸ� ������, �ִٸ� �Ǽ��� ���
				if(calcResult == (int)calcResult) result.setText(Integer.toString((int)calcResult));
				else result.setText(Double.toString(calcResult));
				
				numLeftPar = numRightPar = 0; // ��ȣ ���� �ʱ�ȭ
				
			// 5. ��Ʈ(.) ��ư�� ���� ���
			} else if(clicked.equals(".")) {
				
				// ��Ʈ�� ��� ������ ���
				if(isDotEnabled) {
					if(isOperator(lastStr) || lastStr.equals("(")) result.setText(result.getText() + "0."); // ������ �Է� ���ڿ��� ������ �Ǵ� ���� ��ȣ�� "0." �Է�
					else if(lastStr.equals(")")) result.setText(result.getText() + "*0."); // ������ �Է� ���ڿ��� ������ ��ȣ�� "*0." �Է�
					else result.setText(result.getText() + "."); // �������� ��� "." �Է�
					isDotEnabled = false; // ��Ʈ ����� �Ұ����ϵ��� ����
				}
				
			// 6. ������(*, /, +, -) ��ư�� ���� ���
			} else if(isOperator(clicked)) {
				
				// �ʱ�ȭ ���� ��� �ʱ�ȭ ����
				if(isInit == true)
					isInit = false;
				
				// ������ ���ڿ��� ���� ��ȣ�鼭
				if(lastStr.equals("(")) {
					// �Է��� �����ڰ� -��
					if(clicked.equals("-")) result.setText(result.getText() + clicked); // �Է� ���� ������ �߰�
					// �Է��� �����ڰ� -�� �ƴ϶�� ������ �߰�X
				
				// �� �̿��� ���
				} else {
					// ������ �Է� ���� �����ڶ��, ������ �Էµ� �����ڸ� �����
					if(isOperator(lastStr)) result.setText(origin.substring(0, origin.length()-1));
					result.setText(result.getText() + clicked); // �Է� ���� ������ �߰�
					isDotEnabled = true; // ��Ʈ ����� �����ϵ��� �ʱ�ȭ
				}
								
			// 7. ����(0~9) ��ư�� ���� ���
			} else {
				
				if(isInit == true) { 	// �ʱ�ȭ ���¶��
					result.setText(""); // �Է� ���� ��� ��
					isInit = false; 	// �ʱ�ȭ ���� ����
				}
				result.setText(result.getText() + clicked); // �Է� ���� ���� �߰�
			}
		}
	}
	
	// ���� ��ü ���� �� ǥ��
	public static void main(String[] args) {
		SimpleCalc c = new SimpleCalc();
		c.SwingFrame();
	}

}