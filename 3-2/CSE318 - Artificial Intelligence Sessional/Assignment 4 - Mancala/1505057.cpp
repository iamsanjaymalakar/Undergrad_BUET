#include<iostream>
#include<stdio.h>

using namespace std;

#define MAX 1e9
#define MIN -10000000
#define P1Home 12
#define P2Home 13


int W1=10,W2=10,W3=10,W4=10;


class Mancala
{
private:
    int board[14];
    int index[2][13]= {{0,1,2,3,4,5,12,6,7,8,9,10,11},{6,7,8,9,10,11,13,0,1,2,3,4,5}};
    bool player;
    int additionalMove;
    int stonesCaptured;
public:
    Mancala()
    {
        for(int i=0; i<6; i++) //initialize board
        {
            board[i]=4;
            board[i+6]=4;
        }
        board[P1Home]=0;
        board[P2Home]=0;
        player=false;
        additionalMove=0;
        stonesCaptured=0;
    }

    int select(int position)
    {
        int total=board[index[player][position]];
        board[index[player][position]]=0;
        int j=position+1;
        while(total)
        {
            board[index[player][j]]++;
            if(total==1)
            {
                if(j<6)
                {
                    if(board[index[player][j]]==1)
                    {
                        board[index[player][6]]+=(1+board[index[player][12-j]]); // move to home
                        if(player)
                            stonesCaptured-=board[index[player][12-j]];
                        else
                            stonesCaptured+=board[index[player][12-j]];
                        board[index[player][j]]=0; // clear player bin
                        board[index[player][12-j]]=0; // clear opponent bin
                    }
                    if(player)
                        additionalMove--;
                    else
                        additionalMove++;
                    return 1;
                }
            }
            total--;
            j=(j+1)%13;
        }
        player=!player;
        return 0;
    }

    int isFinished()
    {
        int p1=0,p2=0;
        for(int i=0; i<6; i++)
        {
            p1+=board[i];
            p2+=board[i+6];
        }
        if(!p1) //player 1 zero
        {
            board[P2Home]+=p2;
            for(int i=0; i<6; i++)
                board[i+6]=0;
            return 2;
        }
        if(!p2) //player 2 zero
        {
            board[P1Home]+=p1;
            for(int i=0; i<6; i++)
                board[i]=0;
            return 1;
        }
        return 0;
    }

    int whoWon()
    {
        if(board[P1Home]>board[P2Home]) // player 1 won
            return 1;
        else if(board[P2Home]>board[P1Home]) // player 2 won
            return 2;
        return 0; // draw
    }

    bool getPlayer()
    {
        return player;
    }

    void setAdditionalMove(int value)
    {
        additionalMove=value;
    }

    void setStonesCaptured(int value)
    {
        stonesCaptured=value;
    }

    bool isValid(int position)
    {
        if(board[index[player][position]])
            return true;
        return false;
    }

    int heuristic(int num)
    {
        /* (stones_in_my_storage – stones_in_opponents_storage) */
        if(num==1)
        {
            return board[P1Home]-board[P2Home];
        }
        /* W1 * (stones_in_my_storage – stones_in_opponents_storage) + W2 * (stones_on_my_side –
        stones_on_opponents_side) */
        else if(num==2)
        {
            int p1=0,p2=0;
            for(int i=0; i<6; i++)
            {
                p1+=board[i];
                p2+=board[i+6];
            }
            return W1*(board[P1Home]-board[P2Home])+W2*(p1-p2);
        }
        /* W1 * (stones_in_my_storage – stones_in_opponents_storage) + W2 * (stones_on_my_side –
        stones_on_opponents_side) + W3 * (additional_move_earned) */
        else if(num==3)
        {
            int p1=0,p2=0;
            for(int i=0; i<6; i++)
            {
                p1+=board[i];
                p2+=board[i+6];
            }
            return W1*(board[P1Home]-board[P2Home])+W2*(p1-p2)+W3*additionalMove;
        }
        /* W1 * (stones_in_my_storage – stones_in_opponents_storage) + W2 * (stones_on_my_side –
        stones_on_opponents_side) + W3 * (additional_move_earned) + W4 * (stones_captured) */
        else
        {
            int p1=0,p2=0;
            for(int i=0; i<6; i++)
            {
                p1+=board[i];
                p2+=board[i+6];
            }
            return W1*(board[P1Home]-board[P2Home])+W2*(p1-p2)+W3*additionalMove+
                   W4*stonesCaptured;
        }

    }

    void printBoard()
    {
        cout << "     ";
        for(int i=0; i<6; i++)
            printf("%2d ",board[11-i]);
        cout << endl;
        printf(" %2d                      %2d\n",board[P2Home],board[P1Home]);
        cout << "     ";
        for(int i=0; i<6; i++)
            printf("%2d ",board[i]);
        cout << endl << endl;
    }
};

int maxValue(int depth,Mancala &board,int alpha,int beta,int heu,bool printFlag);
int minValue(int depth,Mancala &board,int alpha,int beta,int heu,bool printFlag);


int maxValue(int depth,Mancala &board,int alpha,int beta,int heu,bool printFlag)
{
    if(depth == 0 || board.isFinished())
    {
        return board.heuristic(heu);
    }
    int index,best=MIN,val;
    for(int i=0; i<6; i++) // generate moves
    {
        Mancala tempBoard = board;
        int moveAgain;
        if(board.isValid(i))
            moveAgain=tempBoard.select(i);
        else
            continue;
        if(moveAgain)
        {
            val = maxValue(depth-1,tempBoard,alpha,beta,heu,false);
        }
        else
        {
            val = minValue(depth-1,tempBoard,alpha,beta,heu,false);
        }
        if(val>best)
        {
            best=val;
            index=i;
            alpha = max(alpha,best);
            if(best>=beta)
                break;
        }
    }
    if(printFlag)
    {
        if(board.getPlayer())
            cout << "Player 2 :" << index << endl;
        else
            cout << "Player 1 :" << index << endl;
    }
    board.select(index);
    return best;
}

int minValue(int depth,Mancala &board,int alpha,int beta,int heu,bool printFlag)
{
    if(depth == 0 || board.isFinished())
    {
        return board.heuristic(heu);
    }
    int index,best=MAX,val;
    for(int i=0; i<6; i++) // generate moves
    {
        Mancala tempBoard = board;
        int moveAgain;
        if(board.isValid(i))
            moveAgain=tempBoard.select(i);
        else
            continue;
        if(moveAgain)
        {
            val = minValue(depth-1,tempBoard,alpha,beta,heu,false);
        }
        else
        {
            val = maxValue(depth-1,tempBoard,alpha,beta,heu,false);
        }
        if(val<best)
        {
            best=val;
            index=i;
            beta = min(beta,best);
            if(best<=alpha)
                break;
        }
    }
    if(printFlag)
    {
        if(board.getPlayer())
            cout << "Player 2 :" << index << endl;
        else
            cout << "Player 1 :" << index << endl;
    }
    board.select(index);
    return best;
}

void alphabeta(int depth,Mancala &board,int alpha,int beta,int heu,bool printFlag)
{
    board.setAdditionalMove(0);
    board.setStonesCaptured(0);
    if(!board.getPlayer())
        maxValue(depth,board,alpha,beta,heu,printFlag);
    else
        minValue(depth,board,alpha,beta,heu,printFlag);
    if(printFlag)
        board.printBoard();
}


int main()
{
    bool single=false;
    if(single)
    {
        Mancala board;
        while(!board.isFinished())
            alphabeta(4,board,MIN,MAX,1,true);
        board.printBoard();
    }
    else
    {
        int p1=0,p2=0,draw=0;
        // heuristic 1
        for(int i=1; i<16; i++)
        {
            Mancala board;
            while(!board.isFinished())
                alphabeta(i,board,MIN,MAX,1,false);
            if(board.whoWon()==1)
                p1++;
            else if(board.whoWon()==2)
                p2++;
            else
                draw++;
        }
        cout << "Total : 15 , Player 1 :" << p1 << " Player 2 : " << p2 <<
             " Draw : " << draw << " Ratio : " << (p1*100)/15 << endl;

        // heuristic 2
        p1=0;
        p2=0;
        draw=0;
        for(int i=1; i<11; i++)
        {
            for(W1=1; W1<4; W1++)
            {
                for(W2=1; W2<4; W2++)
                {
                    Mancala board;
                    while(!board.isFinished())
                        alphabeta(i,board,MIN,MAX,2,false);
                    if(board.whoWon()==1)
                        p1++;
                    else if(board.whoWon()==2)
                        p2++;
                    else
                        draw++;
                }
            }
        }
        cout << "Total : 90 , Player 1 :" << p1 << " Player 2 : " << p2 <<
             " Draw : " << draw << " Ratio : " << (p1*100)/90 << endl;
        // heuristic 3
        p1=0;
        p2=0;
        draw=0;
        for(int i=5; i<10; i++)
        {
            for(W1=1; W1<4; W1++)
            {
                for(W2=1; W2<4; W2++)
                {
                    for(W3=1; W3<4; W3++)
                    {
                            Mancala board;
                        while(!board.isFinished())
                            alphabeta(i,board,MIN,MAX,3,false);
                        if(board.whoWon()==1)
                            p1++;
                        else if(board.whoWon()==2)
                            p2++;
                        else
                            draw++;
                    }
                }
            }
        }
        cout << "Total : 135 , Player 1 :" << p1 << " Player 2 : " << p2 <<
             " Draw : " << draw << " Ratio : " << (p1*100)/135 <<  endl;
        // heuristic 4
        p1=0;
        p2=0;
        draw=0;
        for(int i=5; i<7; i++)
        {
            for(W1=1; W1<4; W1++)
            {
                for(W2=1; W2<4; W2++)
                {
                    for(W3=1; W3<4; W3++)
                    {
                        for(W4=1;W4<4;W4++)
                        {
                            Mancala board;
                        while(!board.isFinished())
                            alphabeta(i,board,MIN,MAX,3,false);
                        if(board.whoWon()==1)
                            p1++;
                        else if(board.whoWon()==2)
                            p2++;
                        else
                            draw++;
                        }
                    }
                }
            }
        }
        cout << "Total : 162 , Player 1 :" << p1 << " Player 2 : " << p2 <<
             " Draw : " << draw << " Ratio : " << (p1*100)/162 <<  endl;
    }
}

