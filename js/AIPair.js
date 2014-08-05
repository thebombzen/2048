function AIPair(board, t1, t2){
	this.board = board;
	this.t1 = t1;
	this.t2 = t2;
}

AIPair.prototype.duplicate = function(){
	return new Pair(this.board, this.t1.duplicate(), this.t2.duplicate());
}

AIPair.prototype.isStronglyCombinable = function() {
	return this.t1.x == this.t2.x && (this.t1.y == this.t2.y || this.t1.y + 1 == this.t2.y || this.t2.y + 1 == this.t1.y) || this.t1.y == this.t2.y && (this.t1.x + 1 == this.t2.x || this.t2.x + 1 == this.t1.x);
};

AIPair.prototype.isWeaklyCombinable = function () {
	var xt; // Temporary
	var yt;
	if (this.t1.x == this.t2.x){
		if (this.t1.y < this.t2.y){
			for (yt = this.t1.y + 1; yt <= this.t2.y - 1; yt++){
				if (this.board.slots[this.t1.x][yt].isNonEmpty()){
					return false;
				}
			}
			return true;
		} else {
			for (yt = this.t2.y + 1; yt <= this.t1.y - 1; yt++){
				if (this.board.slots[this.t1.x][yt].isNonEmpty()){
					return false;
				}
			}
			return true;
		}
	} else if (this.t1.y == this.t2.y){
		if (this.t1.x < this.t2.x){
			for (xt = this.t1.x + 1; xt <= this.t2.x - 1; xt++){
				if (this.board.slots[xt][this.t1.y].isNonEmpty()){
					return false;
				}
			}
			return true;
		} else {
			for (xt = this.t2.x + 1; xt <= this.t1.x - 1; xt++){
				if (this.board.slots[xt][this.t1.y].isNonEmpty()){
					return false;
				}
			}
			return true;
		}
	}
	return false;
}

AIPair.prototype.intersects = function(pair){
	return this.t1.equals(pair.t1) || this.t2.equals(pair.t2) || this.t1.equals(pair.t2) || this.t2.equals(pair.t1);
}

AIPair.prototype.compareTo = function(pair){
	var c = this.t1.compareTo(pair.t1);
	if (c != 0){
		return c;
	}
	return this.t2.compareTo(pair.t2);
}

AIPair.prototype.toString = function(){
	return "{" + this.t1.toString() + ", " + this.t2.toString() + "}";
}

