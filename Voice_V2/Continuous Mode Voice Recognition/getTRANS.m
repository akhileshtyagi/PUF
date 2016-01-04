function r=getTRANS(chainlist)
r = zeros(11*13,11*13);
total = zeros(1,11*13);

num_of_list = size(chainlist,2);


for i = 1 : num_of_list
    chain = chainlist{i};
    num_of_state = size(chain,2);
    %disp(num_of_state);
    
    for j = 1: num_of_state-1
        s = chain(j);
        %disp(s)
        e = chain(j+1);
        if total(1,s)==0
            total(1,s) = 1;
            r(s,e) = 1;            
        else
            total(1,s) = total(1,s)+1;
            for k = 1: 11*13
                if k~=e
                    r(s,k) = (r(s,k)*(total(1,s)-1))/total(1,s);
                else 
                    r(s,e) = (r(s,e)*(total(1,s)-1)+1)/total(1,s);
                end
            end
        end
    end
end

end