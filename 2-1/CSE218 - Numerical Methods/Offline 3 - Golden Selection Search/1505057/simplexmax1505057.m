function [max] = simplexmax()

format short g;

A = [1 -1.75 -1.25 0 0 0 0;0 1.2 2.25 1 0 0 14;0 1 1.1 0 1 0 8;0 2.5 1 0 0 1 9;];

[ra ca]=size(A);

B=zeros(ra,1);

% making tableue with upper bound 
X = [A B];

[rx cx]=size(X);

disp('Initial tableue');
% finding minimum in row 1
min = 0;
indexC = -1;
for i=2:cx-2
    if(X(1,i)<min)
        min=X(1,i);
        indexC=i;
    end
end

% updating uper bound
for i=2:rx
    if(X(i,indexC)~=0)
        X(i,cx)=X(i,cx-1)./X(i,indexC);
    end
end
X

iter=0;

while(true)
    % finding minimum in bound column
    min = X(2,cx);
    indexR = 2;
    for i = 2:rx
        if(X(i,cx)<min)
            min = X(i,cx);
            indexR=i;
        end
    end
   
    % making all the other rows 0
    for i= 1 : rx
        if(i==indexR)
            continue;
        end
        temp=X(i,indexC)./X(indexR,indexC);
        for j = 2 : cx-1
            X(i,j) = X(i,j)-temp.*X(indexR,j);
            if(abs(X(i,j))<(10^-6))
                X(i,j)=0;
            end
        end
    end
    X(1,cx)=0;

    % making 1 in the pivot
    temp=X(indexR,indexC);
    for i = 2 : cx-1
        X(indexR,i)=X(indexR,i)./temp;
    end
    
    % finding minimum in row 1
    min = 0;
    indexC = -1;
    f=0;
    for i=2:cx-2
        if(X(1,i)<min)
            min=X(1,i);
            indexC=i;
            f=1;
        end
    end
 
    if(f==1)
        % updating uper bound
        for i=2:rx
            if(X(i,indexC)~=0)
                X(i,cx)=X(i,cx-1)./X(i,indexC);
            end
        end
    end
    iter=iter+1;
    if(f==1)
        fprintf('After iteration : %d ',iter);
        X
    else
        fprintf('After iteration : %d (Final talbeue)',iter);
        X(:,cx) = [];
        X
        max=X(1,cx-1);
    end
    if(f==0)
        break;
    end
end

end
