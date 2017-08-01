package com.lyl;

import java.util.Arrays;

/**
 *	Χ����������
 */

public class LylRule {
	
	int black_stones;   //����ʱ���Ӹ���������������Ӯ
    int white_stones;   //����ʱ���Ӹ���������������Ӯ

    int unmove_count;   //�ж��Ƿ��������������ÿ��������
    int status;         //���Ӳ��Ϸ���״̬��1��٣�2���ۣ�3����
    
    int board[] = new int[441];         //һά��������
    int temp_board[] = new int[441];    //��ʱһά��������
    int parent[] = new int[441];        //���鼯����
    int temp_parent[] = new int[441];   //��ʱ���鼯����
    int size[] = new int[441];          //���鼯size����
    int temp_size[] = new int[441];     //��ʱ���鼯size����

    int copy_board[] = new int[441];    //��������
    int copy_parent[] = new int[441];   //���ݲ��鼯
    int copy_size[] = new int[441];     //����size

    int eat_flag;                   //��Ա�־
    int te_flag;                    //���۱�־
    LylGoString dragons[] = new LylGoString[441];          //�崮��
    LylGoString temp_dragons[] = new LylGoString[441];     //��ʱ�崮��
    LylGoString copy_dragons[] = new LylGoString[441];     //�����崮��
    LylEatString eatString = new LylEatString();            //�����崮
    LylKoPos ko = new LylKoPos();                       //����λ
    
    
    LylRule() {
    	Arrays.fill(board, 0);		 		//һά���̳�ʼ��
    	
    	for(int i=0; i<21; i++)             //�߽��ʼ��
        {
            board[i] = -1;
            board[i*20+i] = -1;
            board[(i+1)*20+i] = -1;
            board[420+i] = -1;
        }
    	
    	for(int i=0; i<441; i++)        //���鼯��ʼ��
    	{
    		parent[i] = i;
  	        size[i] = 1;
    	}
    	
    	for(int i=0; i<441; i++)		//new dragons
    	{
    		dragons[i] = new LylGoString();
    		temp_dragons[i] = new LylGoString();
    	}
    	
    	black_stones = 0;
        white_stones = 0;

        status = 0;
    }
    
    int find(int p)
    {
    	while(p != temp_parent[p])
        {
            p = temp_parent[temp_parent[p]];    //·��ѹ��
        }
        return p;
    }
    
    void unionElements(int p, int q)
    {
    	int pRoot = find(p);
        int qRoot = find(q);

        if(pRoot == qRoot)
            return;

        if(temp_size[pRoot] <= temp_size[qRoot])    //����size�Ż�
        {
            temp_parent[pRoot] = qRoot;
            temp_size[qRoot] += temp_size[pRoot];
        }
        else
        {
            temp_parent[qRoot] = pRoot;
            temp_size[pRoot] += temp_size[qRoot];
        }
    }
    
    boolean isValidMove(int pos, int player)
    {
    	if(board[pos]==LylDefine.NOSTONE)     //�Ƿ�Ϊ��
        {
            if(pos == ko.pos && player == ko.player)    //����λƥ��
            {
                status = 1;
                return false;
            }
            
            for(int i=0; i<21; i++)             //���³�ʼ���߽磬��Ϊ�ж�����ʱ�����ƻ�
            {
                board[i] = -1;
                board[i*20+i] = -1;
                board[(i+1)*20+i] = -1;
                board[420+i] = -1;
            }

            int lib = 0;                    //��ʱ������
            int op_player = player%2+1;     //�Է�
            eat_flag = 0;
            te_flag = 1;
            eatString.count = 0;

            status = 0;

            //������ʵ���ݸ���ʱ���ݣ��������ֲ���      
            System.arraycopy(parent, 0, temp_parent, 0, parent.length);
            System.arraycopy(size, 0, temp_size, 0, size.length);
            System.arraycopy(board, 0, temp_board, 0, board.length);
            for(int i=0; i<441; i++)
            {
            	temp_dragons[i].player = dragons[i].player;
            	temp_dragons[i].nums = dragons[i].nums;
            	temp_dragons[i].libs = dragons[i].libs;
            	for(int j=0; j<361; j++)
            		temp_dragons[i].dragon[j] = dragons[i].dragon[j];
            }

             temp_board[pos] = player;      //�������ӣ���Ҫ��������Ի���

             if(temp_board[pos-21]==0)
             {
                 lib++;         //ɨ���ϱ��Ƿ�յ㣬���ǣ�����1
                 te_flag = 0;	//����Χ�пյ㣬��������
             }
             if(temp_board[pos+21]==0)
             {
                 lib++;         //ɨ���±��Ƿ�յ㣬���ǣ�����1
                 te_flag = 0;   //����Χ�пյ㣬��������
             }
             if(temp_board[pos-1]==0)
             {
                 lib++;         //ɨ������Ƿ�յ㣬���ǣ�����1
                 te_flag = 0;   //����Χ�пյ㣬��������
             }
             if(temp_board[pos+1]==0)
             {
                 lib++;         //ɨ���ұ��Ƿ�յ㣬���ǣ�����1
                 te_flag = 0;   //����Χ�пյ㣬��������
             }

             addGoString(pos, player, lib);     //��ö���Ӽӽ��崮��

             if(temp_board[pos-21]==player)	//ɨ���ϱ��Ƿ񼺷����ӣ����ǣ��ϲ��崮
             {
                 unionGoString(pos, pos-21);
                 unionElements(pos, pos-21);
             }
             if(temp_board[pos+21]==player)	//ɨ���±��Ƿ񼺷����ӣ����ǣ��ϲ��崮
             {
                 unionGoString(pos, pos+21);
                 unionElements(pos, pos+21);
             }
             if(temp_board[pos-1]==player)	//ɨ���ϱ��Ƿ񼺷����ӣ����ǣ��ϲ��崮
             {
                 unionGoString(pos, pos-1);
                 unionElements(pos, pos-1);
             }
             if(temp_board[pos+1]==player)	//ɨ���ϱ��Ƿ񼺷����ӣ����ǣ��ϲ��崮
             {
                 unionGoString(pos, pos+1);
                 unionElements(pos, pos+1);
             }

             //ɨ���ϱ��Ƿ�Է����ӣ����ǣ��Է��崮����1
             if(temp_board[pos-21] == op_player)
             {
                 int p = find(pos-21);
                 temp_dragons[p].libs--;
                 if(temp_dragons[p].libs == 0)   //�����
                 {
                     eatGoString(p, player);
                     eat_flag = 1;
                 }
                 te_flag = 0;	//����Χ�ǶԷ����ӣ��򲻻�������
             }
             //ɨ���±��Ƿ�Է����ӣ����ǣ��Է��崮����1
             if(temp_board[pos+21] == op_player)
             {
                 int p = find(pos+21);
                 temp_dragons[p].libs--;
                 if(temp_dragons[p].libs == 0)
                 {
                     eatGoString(p, player);
                     eat_flag = 1;
                 }
                 te_flag = 0;   //����Χ�ǶԷ����ӣ��򲻻�������
             }
             //ɨ������Ƿ�Է����ӣ����ǣ��Է��崮����1
             if(temp_board[pos-1] == op_player)
             {
                 int p = find(pos-1);
                 temp_dragons[p].libs--;
                 if(temp_dragons[p].libs == 0)
                 {
                     eatGoString(p, player);
                     eat_flag = 1;
                 }
                 te_flag = 0;   //����Χ�ǶԷ����ӣ��򲻻�������
             }
             //ɨ���ұ��Ƿ�Է����ӣ����ǣ��Է��崮����1
             if(temp_board[pos+1] == op_player)
             {
                 int p = find(pos+1);
                 temp_dragons[p].libs--;
                 if(temp_dragons[p].libs == 0)
                 {
                     eatGoString(p, player);
                     eat_flag = 1;
                 }
                 te_flag = 0;   //����Χ�ǶԷ����ӣ��򲻻�������
             }

             int f = find(pos);
             if(temp_dragons[f].libs>0)     //������
             {
                 ko.pos = -1;   //ȡ������

                 //������������ԶԷ�һ�ӣ���������һ�ӣ���������һ��
                 //��˵�Ϊ�Է����ŵ�
                 if(eatString.count==1 && temp_dragons[f].nums==1 && temp_dragons[f].libs==1)
                 {
                     ko.pos = eatString.eated[0];
                     ko.player = op_player;
                 }

                 //ÿ��������һ��
                 if(unmove_count%2==0)
                 {
//                	 copy_parent = Arrays.copyOf(parent, parent.length);
//                	 copy_size  = Arrays.copyOf(size, size.length);
//                	 copy_board = Arrays.copyOf(board, board.length);
//                	 copy_dragons = Arrays.copyOf(dragons, dragons.length);
                 }
                 unmove_count++;

                 //�Ϸ����ƻ���ʵ����
                 System.arraycopy(temp_parent, 0, parent, 0, parent.length);
                 System.arraycopy(temp_size, 0, size, 0, size.length);
                 System.arraycopy(temp_board, 0, board, 0, board.length);
                 for(int i=0; i<441; i++)
                 {
                 	dragons[i].player = temp_dragons[i].player;
                 	dragons[i].nums = temp_dragons[i].nums;
                 	dragons[i].libs = temp_dragons[i].libs;
                 	for(int j=0; j<361; j++)
                		dragons[i].dragon[j] = temp_dragons[i].dragon[j];
                 }

                 return true;
             }
             else       //����
             {
                 status = 3;
                 return false;
             }
        }
        else
            return false;   //�ǿ�
    }
    
    boolean isEatMove()
    {
    	if(eat_flag==1)
        {
            clearEatString();
            return true;
        }
        else
           return false;
    }
    
    void addGoString(int pos, int player, int lib)
    {	
    	temp_dragons[pos].dragon[0] = pos;
        temp_dragons[pos].player = player;
        temp_dragons[pos].nums = 1;
        temp_dragons[pos].libs = lib;
    }
    
    void unionGoString(int pos1, int pos2)
    {
    	int p = find(pos1);
        int q = find(pos2);

        if(p==q)
        {
            temp_dragons[p].libs--;     //�ǵü�1��������
            return;
        }

        int p_nums = temp_dragons[p].nums;
        int q_nums = temp_dragons[q].nums;

        if(p_nums<=q_nums)      //���ٵĽ����Ӷ�ĺ���
        {
            while(temp_dragons[p].nums != 0)
            {
            	temp_dragons[p].nums--;
            	
            	p_nums--;
                temp_dragons[q].dragon[q_nums] = temp_dragons[p].dragon[p_nums];
                q_nums++;
            }

            temp_dragons[q].nums = q_nums;
            temp_dragons[q].libs = temp_dragons[q].libs + temp_dragons[p].libs - 1;
        }
        else
        {
            while(temp_dragons[q].nums != 0)
            {
            	temp_dragons[q].nums--;
            	
            	q_nums--;
                temp_dragons[p].dragon[p_nums] = temp_dragons[q].dragon[q_nums];
                p_nums++;
            }

            temp_dragons[p].nums = p_nums;
            temp_dragons[p].libs = temp_dragons[p].libs + temp_dragons[q].libs - 1;
        }
    }
    
    void eatGoString(int pos, int player)
    {
    	int eat_nums = temp_dragons[pos].nums;
        int count = eatString.count;

        while(temp_dragons[pos].nums != 0)
        {
        	temp_dragons[pos].nums--;
        	
        	eat_nums--;
            int temp_pos = temp_dragons[pos].dragon[eat_nums];

            eatString.eated[count] = temp_pos;
            count++;

            if(temp_board[temp_pos-21]==player)    //�յ���Χ����
            {
                int t = find(temp_pos-21);
                temp_dragons[t].libs++;
            }
            if(temp_board[temp_pos+21]==player)
            {
                int t = find(temp_pos+21);
                temp_dragons[t].libs++;
            }
            if(temp_board[temp_pos-1]==player)
            {
                int t = find(temp_pos-1);
                temp_dragons[t].libs++;
            }
            if(temp_board[temp_pos+1]==player)
            {
                int t = find(temp_pos+1);
                temp_dragons[t].libs++;
            }
        }
        eatString.count = count;        //�ǵø�ֵ��ȥ������
    }
    
    LylEatString getEatString()
    {
    	return eatString;
    }
    
    void caculateStones()
    {
    	for(int i=0; i<441; i++)
        {
            if(board[i]==LylDefine.BLACK)
                black_stones++;
            if(board[i]==LylDefine.WHITE)
                white_stones++;
            if(board[i]==LylDefine.NOSTONE)
            {
                if(board[i-21]==LylDefine.BLACK || board[i+21]==LylDefine.BLACK || board[i-1]==LylDefine.BLACK || board[i+1]==LylDefine.BLACK)
                    black_stones++;
                if(board[i-21]==LylDefine.WHITE || board[i+21]==LylDefine.WHITE || board[i-1]==LylDefine.WHITE || board[i+1]==LylDefine.WHITE)
                    white_stones++;
            }
        }
    }
    
    void clearEatString()
    {
    	LylEatString es = getEatString();
        int k = es.count;
        while(k != 0)
        {
        	k--;
        	
            int pos = es.eated[k];
            parent[pos] = pos;      //ֱ�Ӹ��²��鼯��size��һά����
            size[pos] = 1;
            board[pos] = LylDefine.NOSTONE;
        }
    }
    
    int[] unMakeMove()
    {
    	
//    	parent = Arrays.copyOf(copy_parent, parent.length);
//   	 	size  = Arrays.copyOf(copy_size, size.length);
//   	 	board = Arrays.copyOf(copy_board, board.length);
//   	 	dragons = Arrays.copyOf(copy_dragons, dragons.length);
  
    	return board;
    }
}
