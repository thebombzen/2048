function AIBoard(){
	this.slots = [];
	for (var x = 0; x < 4; x++){
		var col = [];
		for (var y = 0; y < 4; y++){
			col.push(new AITile(x, y, 0));
		}
		this.slots.push(col);
	}
	this.mostRecentMove = 0;
	this.mostRecentOrthogonalMove = 0;
}

AIBoard.prototype.duplicate = function() {
	var board = new AIBoard();
	for (var x = 0; x < 4; x++){
		for (var y = 0; y < 4; y++){
			board.slots[x][y] = this.slots[x][y].duplicate();
		}
	}
	board.mostRecentMove = this.mostRecentMove;
	board.mostRecentOrthogonalMove = this.mostRecentOrthogonalMove;
	return board;
};

AIBoard.prototype.set = function(position, value) {
	this.slots[position.x][position.y] = new AITile(position, value);
};

AIBoard.prototype.unSet = function(position) {
	this.set(position, 0);
}

AIBoard.prototype.bubbleZeros = function(dir, start, line) {
	var did = false;
	var p, xt, yt;
	switch (dir){
		case 0: // 0
			p = start - 1;
			for (xt = start; xt < 4; xt++){
				if (this.slots[xt][line].isNonEmpty() && ++p != xt){
					this.slots[p][line] = new AITile({x:p, y:line}, this.slots[xt][line].type);
					this.slots[xt][line] = new AITile({x:xt, y:line}, 0);
					did = true;
				}
			}
			break;
		case 1: // 1
			p = start + 1;
			for (xt = start; xt >= 0; xt--){
				if (this.slots[xt][line].isNonEmpty() && --p != xt){
					this.slots[p][line] = new AITile({x:p, y:line}, this.slots[xt][line].type);
					this.slots[xt][line] = new AITile({x:xt, y:line}, 0);
					did = true;
				}
			}
			break;
		case 2: // 2
			p = start - 1;
			for (yt = start; yt < 4; yt++){
				if (this.slots[line][yt].isNonEmpty() && ++p != yt){
					this.slots[line][p] = new AITile({x:line, y:p}, this.slots[line][yt].type);
					this.slots[line][yt] = new AITile({x:line, y:yt}, 0);
					did = true;
				}
			}
			break;
		case 3: // 3
			p = start + 1;
			for (yt = start; yt >= 0; yt--){
				if (this.slots[line][yt].isNonEmpty() && --p != yt){
					this.slots[line][p] = new AITile({x:line, y:p}, this.slots[line][yt].type);
				 	this.slots[line][yt] = new AITile({x:line, y:yt}, 0);
				  	did = true;
				}
			}
			break;
	}
	return did;
}

AIBoard.prototype.moveLine = function(dir, line) {
	var did = false;
	var xt, yt;
	switch (dir){
			case 0: // 0
				for (xt = 0; xt < 3; xt++){
					did |= this.bubbleZeros(dir, xt, line);
					if (this.slots[xt][line].isNonEmpty() && this.slots[xt][line].type == this.slots[xt+1][line].type) {
						this.slots[xt][line] = new AITile({x:xt, y:line}, this.slots[xt][line].type + 1);
						this.slots[xt+1][line] = new AITile({x:xt+1, y:line}, 0);
						did = true;
					}
				}
				break;
			case 1:
				for (xt = 3; xt > 0; xt--){
					did |= this.bubbleZeros(dir, xt, line);
					if (this.slots[xt][line].isNonEmpty() && this.slots[xt][line].type == this.slots[xt-1][line].type){
						this.slots[xt][line] = new AITile({x:xt, y:line}, this.slots[xt][line].type + 1);
						this.slots[xt-1][line] = new AITile({x:xt-1, y:line}, 0);
						did = true;
					}
				}
				break;
			case 2:
				for (yt = 0; yt < 3; yt++){
					did |= this.bubbleZeros(dir, yt, line);
					if (this.slots[line][yt].isNonEmpty() && this.slots[line][yt].type == this.slots[line][yt+1].type){
						this.slots[line][yt] = new AITile({x:line, y:yt}, this.slots[line][yt].type + 1);
						this.slots[line][yt+1] = new AITile({x:line, y:yt+1}, 0);
						did = true;
					}
				}
				break;
			case 3:
				for (yt = 3; yt > 0; yt--){
					did |= this.bubbleZeros(dir, yt, line);
					if (this.slots[line][yt].isNonEmpty() && this.slots[line][yt].type == this.slots[line][yt-1].type){
						this.slots[line][yt] = new AITile({x:line, y:yt}, this.slots[line][yt].type + 1);
						this.slots[line][yt-1] = new AITile({x:line, y:yt-1}, 0);
						did = true;
					}
				}
				break;
		}
		return did;
};

AIBoard.prototype.move = function(dir) {
	var did = false;
	for (var line = 0; line < 4; line++){
		did |= this.moveLine(dir, line);
	}
	if ((dir & 2) != (this.mostRecentMove & 2)){
		this.mostRecentOrthogonalMove = this.mostRecentMove;
	}
	this.mostRecentMove = dir;
	return did;
};

AIBoard.prototype.getAllPairs = function() {
	var ret = [];
	for (var xt = 0; xt < 4; xt++){
		for (var yt = 0; yt < 4; yt++){
			for (var i = xt; i < 4; i++){
				for (var j = yt; j < 4; j++){
					if ((xt != i || yt != j)  && this.slots[xt][yt].isNonEmpty() && this.slots[xt][yt].type == this.slots[i][j].type){
						ret.push(new AIPair(this, this.slots[xt][yt], this.slots[i][j]));
					}
				}
			}
		}
	}
	return ret;
};

AIBoard.prototype.getAvailableTiles = function() {
	var ret = [];
	for (var xt = 0; xt < 4; xt++){
		for (var yt = 0; yt < 4; yt++){
			if (this.slots[xt][yt].isEmpty()){
				ret.push(this.slots[xt][yt]);
			}
		}
	}
	return ret;
};

AIBoard.prototype.getMaximumNumberOfPairsSatisfying = function(tester) {
		
	var pairs = this.getAllPairs();
	// The sorted part is crucial because otherwise it fails
	var found = new SortedSet({comparator: function(a, b) { return a.compareTo(b); } });

	// This stores the degree of the vertex
	var used = new Object();
	for (var xt = 0; xt < 4; xt++){
		for (var yt = 0; yt < 4; yt++){
			used[this.slots[xt][yt].toString()] = 0;
		}
	}

	var i;
	for (i = 0; i < pairs.length; i++){
		pair = pairs[i];
		if (tester(pair)){
			found.insert(pair);
			++used[pair.t1.toString()];
			++used[pair.t2.toString()];
		}
	}

	var actuallyFound = 0;

	var discovered = false;

	var discPair;
	var pair;
	var pairIter;
	var tempFound = false;
	do {
		do {
			discovered = false;
			pairIter = found.beginIterator();
			while (pairIter.hasNext()) {
				pair = pairIter.value();
				pairIter = pairIter.next();
				//alert(pair.toString());
				if (((used[pair.t1.toString()] | used[pair.t2.toString()]) & 1) != 0){
					actuallyFound++;
					discovered = true;
					discPair = pair;
					break;
				}
			}
			if (discovered){
				do {
					tempFound = false;
					pairIter = found.beginIterator();
					while (pairIter.hasNext()){
						pair = pairIter.value();
						pairIter = pairIter.next();
						if (pair.intersects(discPair)){
							--used[pair.t1.toString()];
							--used[pair.t2.toString()];
							found.remove(pair);
							tempFound = true;
							break;
						}
					}
				} while (tempFound);
					
			}
		} while (discovered);

		if (found.beginIterator().hasNext()){
			discPair = found.beginIterator().value();
			actuallyFound++;
			do {
				tempFound = false;
				pairIter = found.beginIterator();
				while (pairIter.hasNext()){
					pair = pairIter.value();
					pairIter = pairIter.next();
					if (pair.intersects(discPair)){
						--used[pair.t1.toString()];
						--used[pair.t2.toString()];
						found.remove(pair);
						tempFound = true;
						break;
					}
				}
			} while (tempFound);
		}

	} while (found.beginIterator().hasNext());

	return actuallyFound;
};

AIBoard.prototype.getBoardCombinability = function() {
	return this.getMaximumNumberOfPairsSatisfying(function(pair) { return pair.isWeaklyCombinable(); });
};

AIBoard.prototype.getBoardBlockableCombinability = function () {
	return this.getMaximumNumberOfPairsSatisfying(function(pair) { return !pair.isStronglyCombinable() && pair.isWeaklyCombinable();});
};

AIBoard.prototype.getBoardSecondaryCombinability = function () {
	var highest = 0;
	for (var dir = 0; dir < 4; dir++){
		var board = this.duplicate();
		board.move(dir);
		var currHighest = board.getBoardCombinability() - (board.getBoardBlockableCombinability() > 0 ? 1 : 0);
		if (currHighest > highest){
			return highest = currHighest;
		}
	}
	return highest;
}

AIBoard.prototype.getBoardNCombinability = function(n) {
	return this.getMaximumNumberOfPairsSatisfying(function(pair) { return pair.t1.type == n && pair.isWeaklyCombinable(); });
};

AIBoard.prototype.getBoardBlockableNCombinability = function (n) {
	return this.getMaximumNumberOfPairsSatisfying(
		function(pair) {
			return pair.t1.type == n && !pair.isStronglyCombinable() && pair.isWeaklyCombinable();
		}
	);
};

AIBoard.prototype.getBoardSecondaryNCombinability = function (n) {
	var highest = 0;
	for (var dir = 0; dir < 4; dir++){
		var board = this.duplicate();
		board.move(dir);
		var currHighest = board.getBoardNCombinability(n) - (board.getBoardBlockableNCombinability(n) > 0 ? 1 : 0);
		if (currHighest > highest){
			return highest = currHighest;
		}
	}
	return highest;
}

AIBoard.prototype.getMostExtreme = function(tiles, dir, close) {
	var best;
	if ((dir == 0 || dir == 2) == close){
		best = 4; // this is actually the worst
	} else {
		best = -1;
	}
	var ret = [];
	var tile;
	var i;
	switch (dir){
		case 0:
			for (i = 0; i < tiles.length; i++){
				tile = tiles[i];
				if (close && (tile.x < best) || !close && (tile.x > best)){
					best = tile.x;
					ret = [];
					ret.push(tile);
				} else if (tile.x == best){
					ret.push(tile);
				}
			}
			return ret;
		case 1:
			for (i = 0; i < tiles.length; i++){
				tile = tiles[i];
				if (close && (tile.x > best) || !close && (tile.x < best)){
					best = tile.x;
					ret = [];
					ret.push(tile);
				} else if (tile.x == best){
					ret.push(tile);
				}
			}
			return ret;
		case 2:
			for (i = 0; i < tiles.length; i++){
				tile = tiles[i];
				if (close && (tile.y < best) || !close && (tile.x > best)){
					best = tile.y;
					ret = [];
					ret.push(tile);
				} else if (tile.y == best){
					ret.push(tile);
				}
			}
			return ret;
		case 3:
			for (i = 0; i < tiles.length; i++){
				tile = tiles[i];
				if (close && (tile.y > best) || !close && (tile.x < best)){
					best = tile.y;
					ret = [];
					ret.push(tile);
				} else if (tile.y == best){
					ret.push(tile);
				}
			}
			return ret;
		default:
			return ret; // will never happen
	}
};


AIBoard.prototype.getMeanComputerMove = function() {
	//return getRandomMove();
	var prevAvail = [];
	var currAvail = [];

	var tile;
	var i;
	var tiles = this.getAvailableTiles();
	for (i = 0; i < tiles.length; i++){
		prevAvail.push(new AITile(tiles[i], 1));
		prevAvail.push(new AITile(tiles[i], 2));
	}

	//console.log("=================");

	// Which tiles can we place without any twos combining?
	var best = 1000000;
	var num;
	for (i = 0; i < prevAvail.length; i++){
		tile = prevAvail[i];
		this.slots[tile.x][tile.y] = tile;
		num = this.getBoardNCombinability(1);
		//console.log(tile + ", Two1, " + num);
		//alert(tile.toString() + num);
		//System.out.println(tile + ", Two1, " + num);
		if (num < best){
			best = num;
			currAvail = [];
			currAvail.push(tile);
		} else if (num == best) {
			currAvail.push(tile);
		}
		this.unSet(tile);
	}
	if (currAvail.length == 1){
		return currAvail[0];
	} else if (currAvail.length > 1) {
		prevAvail = currAvail.slice(0);
		currAvail = [];
	}

	// Which tiles can we place without twos secondarily combining?
	best = 10000000;
	for (i = 0; i < prevAvail.length; i++){
		tile = prevAvail[i];
		this.slots[tile.x][tile.y] = tile;
		num = this.getBoardSecondaryNCombinability(1);
		//System.out.println(tile + ", Two1, " + num);
		//console.log(tile + ", Two2, " + num);
		if (num < best){
			best = num;
			currAvail = [];
			currAvail.push(tile);
		} else if (num == best) {
			currAvail.push(tile);
		}
		this.unSet(tile);
	}
	if (currAvail.length == 1){
		return currAvail[0];
	} else if (currAvail.length > 1) {
		prevAvail = currAvail.slice(0);
		currAvail = [];
	}

	// Which tiles are twos?
	for (i = 0; i < prevAvail.length; i++){
		tile = prevAvail[i];
		if (tile.type == 1){
			currAvail.push(tile);
		}
	}
	if (currAvail.length == 1){
		return currAvail[0];
	} else if (currAvail.length > 1) {
		prevAvail = currAvail.slice(0);
		currAvail = [];
	}

	// Which tiles can we place without anything combining?
	best = 1000000;
	for (i = 0; i < prevAvail.length; i++){
		tile = prevAvail[i];
		this.slots[tile.x][tile.y] = tile;
		num = this.getBoardCombinability();
		//System.out.println(tile + ", Two1, " + num);
		console.log(tile + ", Any1, " + num);
		if (num < best){
			best = num;
			currAvail = [];
			currAvail.push(tile);
		} else if (num == best) {
			currAvail.push(tile);
		}
		this.unSet(tile);
	}
	if (currAvail.length == 1){
		return currAvail[0];
	} else if (currAvail.length > 1) {
		prevAvail = currAvail.slice(0);
		currAvail = [];
	}

	best = 1000000;
	// Which tiles prevent anything from combining secondarily? 
	for (i = 0; i < prevAvail.length; i++){
		tile = prevAvail[i];
		this.slots[tile.x][tile.y] = tile;
		num = this.getBoardSecondaryCombinability();
		//System.out.println(tile + ", Two1, " + num);
		//console.log(tile + ", Any2, " + num);
		if (num < best){
			best = num;
			currAvail = [];
			currAvail.push(tile);
		} else if (num == best) {
			currAvail.push(tile);
		}
		this.unSet(tile);
	}
	if (currAvail.length == 1){
		return currAvail[0];
	} else if (currAvail.length > 1) {
		prevAvail = currAvail.slice(0);
		currAvail = [];
	}

	return this.getMostExtreme(this.getMostExtreme(prevAvail, this.mostRecentOrthogonalMove, true), this.mostRecentMove, true)[0];
};

AIBoard.prototype.getNiceComputerMove = function() {
	//return getRandomMove();
	var prevAvail = [];
	var currAvail = [];

	var tile;
	var i;
	var tiles = this.getAvailableTiles();
	for (i = 0; i < tiles.length; i++){
		prevAvail.push(new AITile(tiles[i], 1));
		prevAvail.push(new AITile(tiles[i], 2));
	}

	//console.log("=================");

	// Which tiles can we place without any twos combining?
	var best = -1;
	var num;
	for (i = 0; i < prevAvail.length; i++){
		tile = prevAvail[i];
		this.slots[tile.x][tile.y] = tile;
		num = this.getBoardNCombinability(1);
		//console.log(tile + ", Two1, " + num);
		//alert(tile.toString() + num);
		//System.out.println(tile + ", Two1, " + num);
		if (num > best){
			best = num;
			currAvail = [];
			currAvail.push(tile);
		} else if (num == best) {
			currAvail.push(tile);
		}
		this.unSet(tile);
	}
	if (currAvail.length == 1){
		return currAvail[0];
	} else if (currAvail.length > 1) {
		prevAvail = currAvail.slice(0);
		currAvail = [];
	}

	// Which tiles can we place without twos secondarily combining?
	best = -1;
	for (i = 0; i < prevAvail.length; i++){
		tile = prevAvail[i];
		this.slots[tile.x][tile.y] = tile;
		num = this.getBoardSecondaryNCombinability(1);
		//System.out.println(tile + ", Two1, " + num);
		//console.log(tile + ", Two2, " + num);
		if (num > best){
			best = num;
			currAvail = [];
			currAvail.push(tile);
		} else if (num == best) {
			currAvail.push(tile);
		}
		this.unSet(tile);
	}
	if (currAvail.length == 1){
		return currAvail[0];
	} else if (currAvail.length > 1) {
		prevAvail = currAvail.slice(0);
		currAvail = [];
	}

	// Which tiles can we place without anything combining?
	best = -1;
	for (i = 0; i < prevAvail.length; i++){
		tile = prevAvail[i];
		this.slots[tile.x][tile.y] = tile;
		num = this.getBoardCombinability();
		//System.out.println(tile + ", Two1, " + num);
		//console.log(tile + ", Any1, " + num);
		if (num > best){
			best = num;
			currAvail = [];
			currAvail.push(tile);
		} else if (num == best) {
			currAvail.push(tile);
		}
		this.unSet(tile);
	}
	if (currAvail.length == 1){
		return currAvail[0];
	} else if (currAvail.length > 1) {
		prevAvail = currAvail.slice(0);
		currAvail = [];
	}

	best = -1;
	// Which tiles prevent anything from combining secondarily? 
	for (i = 0; i < prevAvail.length; i++){
		tile = prevAvail[i];
		this.slots[tile.x][tile.y] = tile;
		num = this.getBoardSecondaryCombinability();
		//System.out.println(tile + ", Two1, " + num);
		//console.log(tile + ", Any2, " + num);
		if (num > best){
			best = num;
			currAvail = [];
			currAvail.push(tile);
		} else if (num == best) {
			currAvail.push(tile);
		}
		this.unSet(tile);
	}
	if (currAvail.length == 1){
		return currAvail[0];
	} else if (currAvail.length > 1) {
		prevAvail = currAvail.slice(0);
		currAvail = [];
	}

	return this.getMostExtreme(this.getMostExtreme(prevAvail, this.mostRecentOrthogonalMove, false), this.mostRecentMove, false)[0];
};

AIBoard.prototype.getComputerMove = function() {	 
	var total = 0;
	for (var x = 0; x < 4; x++){
		for (var y = 0; y < 4; y++){
			if (this.slots[x][y].isEmpty()){
				continue;
			}
			total += (1 << this.slots[x][y].type);
			if (this.slots[x][y].type == 10 || total > 1536){
				return this.getMeanComputerMove();
			}
		}
	}
	return this.getNiceComputerMove();
};

AIBoard.prototype.toString = function(){
	var ret = "[";
	for (var x = 0; x < 4; x++){
		ret += "[";
		for (var y = 0; y < 4; y++){
			ret += this.slots[x][y].type + ", ";
		}
		ret += "], ";
	}
	ret += "]";
	return ret;
}