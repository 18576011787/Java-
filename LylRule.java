package com.lyl;

import java.util.Arrays;

/**
 *	围棋规则核心类
 */

public class LylRule {
	
	int black_stones;   //终盘时黑子个数，用来计算输赢
    int white_stones;   //终盘时白子个数，用来计算输赢

    int unmove_count;   //判断是否满足悔棋条件，每两步备份
    int status;         //落子不合法的状态，1打劫，2真眼，3无气
    
    int board[] = new int[441];         //一维棋盘数组
    int temp_board[] = new int[441];    //临时一维棋盘数组
    int parent[] = new int[441];        //并查集数组
    int temp_parent[] = new int[441];   //临时并查集数组
    int size[] = new int[441];          //并查集size数组
    int temp_size[] = new int[441];     //临时并查集size数组

    int copy_board[] = new int[441];    //备份棋盘
    int copy_parent[] = new int[441];   //备份并查集
    int copy_size[] = new int[441];     //备份size

    int eat_flag;                   //打吃标志
    int te_flag;                    //真眼标志
    LylGoString dragons[] = new LylGoString[441];          //棋串表
    LylGoString temp_dragons[] = new LylGoString[441];     //临时棋串表
    LylGoString copy_dragons[] = new LylGoString[441];     //备份棋串表
    LylEatString eatString = new LylEatString();            //被吃棋串
    LylKoPos ko = new LylKoPos();                       //劫争位
    
    
    LylRule() {
    	Arrays.fill(board, 0);		 		//一维棋盘初始化
    	
    	for(int i=0; i<21; i++)             //边界初始化
        {
            board[i] = -1;
            board[i*20+i] = -1;
            board[(i+1)*20+i] = -1;
            board[420+i] = -1;
        }
    	
    	for(int i=0; i<441; i++)        //并查集初始化
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
            p = temp_parent[temp_parent[p]];    //路径压缩
        }
        return p;
    }
    
    void unionElements(int p, int q)
    {
    	int pRoot = find(p);
        int qRoot = find(q);

        if(pRoot == qRoot)
            return;

        if(temp_size[pRoot] <= temp_size[qRoot])    //基于size优化
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
    	if(board[pos]==LylDefine.NOSTONE)     //是否为空
        {
            if(pos == ko.pos && player == ko.player)    //劫争位匹配
            {
                status = 1;
                return false;
            }
            
            for(int i=0; i<21; i++)             //重新初始化边界，因为判断真眼时产生破坏
            {
                board[i] = -1;
                board[i*20+i] = -1;
                board[(i+1)*20+i] = -1;
                board[420+i] = -1;
            }

            int lib = 0;                    //临时气变量
            int op_player = player%2+1;     //对方
            eat_flag = 0;
            te_flag = 1;
            eatString.count = 0;

            status = 0;

            //复制真实数据给临时数据，用来各种操作      
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

             temp_board[pos] = player;      //己方落子，主要方便计算打吃还气

             if(temp_board[pos-21]==0)
             {
                 lib++;         //扫描上边是否空点，若是，气加1
                 te_flag = 0;	//若周围有空点，则不是真眼
             }
             if(temp_board[pos+21]==0)
             {
                 lib++;         //扫描下边是否空点，若是，气加1
                 te_flag = 0;   //若周围有空点，则不是真眼
             }
             if(temp_board[pos-1]==0)
             {
                 lib++;         //扫描左边是否空点，若是，气加1
                 te_flag = 0;   //若周围有空点，则不是真眼
             }
             if(temp_board[pos+1]==0)
             {
                 lib++;         //扫描右边是否空点，若是，气加1
                 te_flag = 0;   //若周围有空点，则不是真眼
             }

             addGoString(pos, player, lib);     //单枚棋子加进棋串表

             if(temp_board[pos-21]==player)	//扫描上边是否己方棋子，若是，合并棋串
             {
                 unionGoString(pos, pos-21);
                 unionElements(pos, pos-21);
             }
             if(temp_board[pos+21]==player)	//扫描下边是否己方棋子，若是，合并棋串
             {
                 unionGoString(pos, pos+21);
                 unionElements(pos, pos+21);
             }
             if(temp_board[pos-1]==player)	//扫描上边是否己方棋子，若是，合并棋串
             {
                 unionGoString(pos, pos-1);
                 unionElements(pos, pos-1);
             }
             if(temp_board[pos+1]==player)	//扫描上边是否己方棋子，若是，合并棋串
             {
                 unionGoString(pos, pos+1);
                 unionElements(pos, pos+1);
             }

             //扫描上边是否对方棋子，若是，对方棋串气减1
             if(temp_board[pos-21] == op_player)
             {
                 int p = find(pos-21);
                 temp_dragons[p].libs--;
                 if(temp_dragons[p].libs == 0)   //若打吃
                 {
                     eatGoString(p, player);
                     eat_flag = 1;
                 }
                 te_flag = 0;	//若周围是对方棋子，则不会是真眼
             }
             //扫描下边是否对方棋子，若是，对方棋串气减1
             if(temp_board[pos+21] == op_player)
             {
                 int p = find(pos+21);
                 temp_dragons[p].libs--;
                 if(temp_dragons[p].libs == 0)
                 {
                     eatGoString(p, player);
                     eat_flag = 1;
                 }
                 te_flag = 0;   //若周围是对方棋子，则不会是真眼
             }
             //扫描左边是否对方棋子，若是，对方棋串气减1
             if(temp_board[pos-1] == op_player)
             {
                 int p = find(pos-1);
                 temp_dragons[p].libs--;
                 if(temp_dragons[p].libs == 0)
                 {
                     eatGoString(p, player);
                     eat_flag = 1;
                 }
                 te_flag = 0;   //若周围是对方棋子，则不会是真眼
             }
             //扫描右边是否对方棋子，若是，对方棋串气减1
             if(temp_board[pos+1] == op_player)
             {
                 int p = find(pos+1);
                 temp_dragons[p].libs--;
                 if(temp_dragons[p].libs == 0)
                 {
                     eatGoString(p, player);
                     eat_flag = 1;
                 }
                 te_flag = 0;   //若周围是对方棋子，则不会是真眼
             }

             int f = find(pos);
             if(temp_dragons[f].libs>0)     //若有气
             {
                 ko.pos = -1;   //取消劫争

                 //劫争条件：打吃对方一子，己方仅有一子，己方仅有一气
                 //则此点为对方禁着点
                 if(eatString.count==1 && temp_dragons[f].nums==1 && temp_dragons[f].libs==1)
                 {
                     ko.pos = eatString.eated[0];
                     ko.player = op_player;
                 }

                 //每两步备份一次
                 if(unmove_count%2==0)
                 {
//                	 copy_parent = Arrays.copyOf(parent, parent.length);
//                	 copy_size  = Arrays.copyOf(size, size.length);
//                	 copy_board = Arrays.copyOf(board, board.length);
//                	 copy_dragons = Arrays.copyOf(dragons, dragons.length);
                 }
                 unmove_count++;

                 //合法则复制回真实数据
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
             else       //无气
             {
                 status = 3;
                 return false;
             }
        }
        else
            return false;   //非空
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
            temp_dragons[p].libs--;     //记得减1气！！！
            return;
        }

        int p_nums = temp_dragons[p].nums;
        int q_nums = temp_dragons[q].nums;

        if(p_nums<=q_nums)      //子少的接在子多的后面
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

            if(temp_board[temp_pos-21]==player)    //空点周围还气
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
        eatString.count = count;        //记得赋值回去！！！
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
            parent[pos] = pos;      //直接更新并查集，size，一维棋盘
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
