create or replace function search(ide int, d int)
	returns table(id int, link int , depth int , path int[], cycle bool) as $$
	with recursive search_graph(id, link, depth, path, cycle) as (
		select g.user_id, g.friend_id , 1, ARRAY[g.user_id, g.friend_id],
          false
		 from user_friends_mapping g where g.user_id = ide
		union
		select g.user_id, g.friend_id, sg.depth + 1,path || g.friend_id,
          g.friend_id = ANY(path)
		from user_friends_mapping g, search_graph sg
		where sg.link = g.user_id and sg.depth <= d and not cycle
    )
SELECT id, link, depth, path, cycle FROM search_graph where not cycle and array_length(path,1) <= d+1;
$$ language sql;